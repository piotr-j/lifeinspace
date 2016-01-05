package io.piotrjastrzebski.lis.game.processors

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Disposable
import io.piotrjastrzebski.lis.game.components.Culled
import io.piotrjastrzebski.lis.game.components.RenderableModel
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Resizing

/**
 * Created by PiotrJ on 22/12/15.
 */
class ModelRenderer() : IteratingSystem(Aspect.all(RenderableModel::class.java).exclude(Culled::class.java)), SubRenderer, Resizing {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var mRenderableModel: ComponentMapper<RenderableModel>
    @Wire lateinit var keybinds: KeyBindings

    internal val GROUND_FLAG = 1 shl 8
    internal val OBJECT_FLAG = 1 shl 9
    internal val ALL_FLAG = -1

    val modelBatch = ModelBatch()
    val environment = Environment()
    var instances = com.badlogic.gdx.utils.Array<ModelInstance>()
    private var debugInstances = com.badlogic.gdx.utils.Array<GameObject>()

    var debugCamera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    var camController = CameraInputController(debugCamera)

    var debug = false
    public lateinit var currentCamera: Camera
    private fun toggleDebug(): Boolean {
        debug = !debug
        currentCamera = if (debug) debugCamera else camera
        camController.camera = currentCamera
        return true
    }

    private lateinit var debugFrustumModel: Model
    private lateinit var debugFrustumInstance: ModelInstance
    private lateinit var testModel: Model

    val collisionConfig: btCollisionConfiguration;
    val dispatcher: btDispatcher
    val broadPhase: btDbvtBroadphase
    val constraintSolver: btSequentialImpulseConstraintSolver
    val dynamicsWorld: btDiscreteDynamicsWorld

    internal inner class MyContactListener : ContactListener() {
        // note this is magic with static stuff, automagically added to bullet
        // note only one method can be overridden, jni magic
        override fun onContactAdded(userValue0: Int, partId0: Int, index0: Int, match0: Boolean,
                           userValue1: Int, partId1: Int, index1: Int, match1: Boolean): Boolean {
            if (match0)
                (instances.get(userValue0).materials.get(0).get(ColorAttribute.Diffuse) as ColorAttribute).color.set(Color.WHITE)
            if (match1)
                (instances.get(userValue1).materials.get(0).get(ColorAttribute.Diffuse) as ColorAttribute).color.set(Color.WHITE)
            return true
        }
    }
    private val contactListener: MyContactListener

    internal class MyMotionState : btMotionState() {
        lateinit var transform: Matrix4
        override fun getWorldTransform (worldTrans: Matrix4) {
            worldTrans.set(transform);
        }

        override fun setWorldTransform (worldTrans: Matrix4) {
            transform.set(worldTrans);
        }
    }

    internal class GameObject(model: Model, node: String, constructionInfo: btRigidBody.btRigidBodyConstructionInfo) : ModelInstance(model, node), Disposable {
        val body: btRigidBody
        val motionState: MyMotionState

        init {
            motionState = MyMotionState()
            motionState.transform = transform
            body = btRigidBody(constructionInfo)
            body.motionState = motionState
        }

        override fun dispose() {
            body.dispose()
            motionState.dispose()
        }

        internal class Constructor(val model: Model, val node: String, val shape: btCollisionShape, mass: Float) : Disposable {
            val constructionInfo: btRigidBody.btRigidBodyConstructionInfo

            init {
                if (mass > 0f)
                    shape.calculateLocalInertia(mass, localInertia)
                else
                    localInertia.set(0f, 0f, 0f)
                this.constructionInfo = btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia)
            }

            fun construct(): GameObject {
                return GameObject(model, node, constructionInfo)
            }

            override fun dispose() {
                shape.dispose()
                constructionInfo.dispose()
            }
            // static vector. fun
            companion object {
                private val localInertia = Vector3()
            }
        }
    }

    private var constructors = ArrayMap<String, GameObject.Constructor>(String::class.java, GameObject.Constructor::class.java)

    private var ground: GameObject

    init {
        collisionConfig = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfig)
        broadPhase = btDbvtBroadphase()
        constraintSolver = btSequentialImpulseConstraintSolver()
        dynamicsWorld = btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, collisionConfig)
        dynamicsWorld.gravity = Vector3(0f, 0f, -3f)
        contactListener = MyContactListener()

        val mb = ModelBuilder()
        mb.begin();
        mb.node().id = "ground";
        mb.part("ground", GL20.GL_TRIANGLES,
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong(),
                Material(ColorAttribute.createDiffuse(Color.RED)))
        .box(10f, 10f, 1f);
        mb.node().id = "sphere";
        mb.part(
                "sphere", GL20.GL_TRIANGLES,
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong(),
                Material(ColorAttribute.createDiffuse(Color.GREEN)))
                .sphere(1f, 1f, 1f, 10, 10);
        testModel = mb.end();

        constructors.put("ground", GameObject.Constructor(testModel, "ground", btBoxShape(Vector3(5f, 5f, .5f)), 0f))
        constructors.put("sphere", GameObject.Constructor(testModel, "sphere", btSphereShape(0.5f), 1f))

        ground = constructors.get("ground").construct()
        dynamicsWorld.addRigidBody(ground.body)
        ground.body.contactCallbackFlag = GROUND_FLAG
        ground.body.contactCallbackFilter = 0
        debugInstances.add(ground)
    }

    fun spawn() {
        val obj = constructors.values[1].construct()
        obj.transform.setFromEulerAngles(MathUtils.random(360f), MathUtils.random(360f), MathUtils.random(360f))
        obj.transform.trn(MathUtils.random(-2.5f, 2.5f), MathUtils.random(-2.5f, 2.5f), 6f)
        obj.body.proceedToTransform(obj.transform)
        obj.body.userValue = debugInstances.size
        obj.body.collisionFlags = obj.body.collisionFlags or btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK
        debugInstances.add(obj)
        dynamicsWorld.addRigidBody(obj.body)
        obj.body.contactCallbackFlag = OBJECT_FLAG
        obj.body.contactCallbackFilter = GROUND_FLAG
    }

    override fun initialize() {
        keybinds.register(this, Input.Keys.F4, {toggleDebug()}, {false})
        currentCamera = camera

        debugCamera.position.set(0f, -10f, 10f)
        debugCamera.lookAt(0f, 0f, 0f)
        debugCamera.near = 1f
        debugCamera.far = 1000f
        debugCamera.update()

        // setup cam so the model is built properly
        camera.near = 1f
        camera.far = 15f
        camera.position.x = 0f
        camera.position.y = 0f
        camera.position.z = 10f
        camera.lookAt(0f, 0f, 0f)
        camera.update()
        debugFrustumModel = createFrustumModel(camera)
        debugFrustumInstance = ModelInstance(debugFrustumModel)

//        camera.position.x = -10f
        // TODO this looks kinda fine, but we want perspective cam for debug purposes
        camera.position.x = 0f
//        camera.position.y = -15f
        camera.position.z = 10f
        camera.lookAt(0f, 0f, 0f)
        camera.update()

        camController.camera = currentCamera

        val multi = Gdx.input.inputProcessor as InputMultiplexer
        multi.addProcessor(0, camController)

        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f))
    }

    private var spawnTimer = 0f;

    override fun render() {
        val dt = Math.min(1f / 30f, world.delta);
        dynamicsWorld.stepSimulation(dt, 5, 1f/60f)
        spawnTimer -= world.delta
        if (spawnTimer < 0) {
            spawn()
            spawnTimer = 1.5f
        }

        camController.update()
        modelBatch.begin(currentCamera)
        modelBatch.render<ModelInstance>(instances, environment)
        modelBatch.render<ModelInstance>(debugInstances, environment)
        if (debug) {
            debugFrustumInstance.transform.set(camera.view).inv()
            modelBatch.render(debugFrustumInstance, environment)
        }
        modelBatch.end()
    }

    override fun begin() {
        // clear instances, add all the crap
        instances.size = 0
    }

    override fun process(entityId: Int) {
        instances.add(mRenderableModel.get(entityId).instance)
    }

    fun createFrustumModel(cam: OrthographicCamera): Model {
        val builder = ModelBuilder()
        builder.begin()
        val mpb = builder.part("", GL20.GL_LINES,
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong(),
                Material(ColorAttribute(ColorAttribute.Diffuse, Color.WHITE)))

        val up = cam.up
        val right = Vector3(up).crs(cam.direction)
        val dirNear = Vector3(cam.direction).scl(cam.near)
        val dirFar = Vector3(cam.direction).scl(cam.far)

        val height = Vector3(up).scl(cam.viewportHeight/2)
        val width = Vector3(up).crs(cam.direction).scl(cam.viewportWidth/2)

        // NOTE this could be a box
        mpb.vertex(
                dirNear.x + width.x + height.x, dirNear.y + width.y + height.y, dirNear.z + width.z + height.z, 0f, 0f, 1f,
                dirNear.x + width.x - height.x, dirNear.y + width.y - height.y, dirNear.z + width.z - height.z, 0f, 0f, 1f,
                dirNear.x - width.x - height.x, dirNear.y - width.y - height.y, dirNear.z - width.z - height.z, 0f, 0f, 1f,
                dirNear.x - width.x + height.x, dirNear.y - width.y + height.y, dirNear.z - width.z + height.z, 0f, 0f, 1f,

                dirFar.x + width.x + height.x, dirFar.y + width.y + height.y, dirFar.z + width.z + height.z, 0f, 0f, 1f,
                dirFar.x + width.x - height.x, dirFar.y + width.y - height.y, dirFar.z + width.z - height.z, 0f, 0f, 1f,
                dirFar.x - width.x - height.x, dirFar.y - width.y - height.y, dirFar.z - width.z - height.z, 0f, 0f, 1f,
                dirFar.x - width.x + height.x, dirFar.y - width.y + height.y, dirFar.z - width.z + height.z, 0f, 0f, 1f,

                // center marker
               + up.x/2 - right.x/2, + up.y/2 - right.y/2, + up.z/2 - right.z/2, 0f, 0f, 1f,
               - up.x/2 + right.x/2, - up.y/2 + right.y/2, - up.z/2 + right.z/2, 0f, 0f, 1f,
               + up.x/2 + right.x/2, + up.y/2 + right.y/2, + up.z/2 + right.z/2, 0f, 0f, 1f,
               - up.x/2 - right.x/2, - up.y/2 - right.y/2, - up.z/2 - right.z/2, 0f, 0f, 1f
        )
        mpb.index(0, 1, 1, 2, 2, 3, 3, 0)
        mpb.index(4, 5, 5, 6, 6, 7, 7, 4)
        mpb.index(0, 4, 1, 5, 2, 6, 3, 7)
        mpb.index(8, 9, 10, 11)
        return builder.end()
    }

    override fun dispose() {
        debugFrustumModel.dispose()
        modelBatch.dispose()

        dispatcher.dispose()
        collisionConfig.dispose()

    }

    override fun resize(width: Int, height: Int) {
        debugFrustumModel.dispose()
        // lets hope whatever is using the cam will position it properly
        camera.position.x = 0f
        camera.position.y = 0f
        camera.position.z = 10f
        camera.lookAt(0f, 0f, 0f)
        camera.update()
        debugFrustumModel = createFrustumModel(camera)
        debugFrustumInstance = ModelInstance(debugFrustumModel)
    }
}
