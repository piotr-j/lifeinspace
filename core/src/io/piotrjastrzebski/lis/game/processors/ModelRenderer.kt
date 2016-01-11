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
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Disposable
import io.piotrjastrzebski.lis.game.components.Culled
import io.piotrjastrzebski.lis.game.components.RenderableModel
import io.piotrjastrzebski.lis.game.processors.physics.BulletContactListener
import io.piotrjastrzebski.lis.game.processors.physics.BulletMotionState
import io.piotrjastrzebski.lis.game.processors.physics.Physics
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.utils.Resizing

/**
 * Created by PiotrJ on 22/12/15.
 */
class ModelRenderer() : IteratingSystem(Aspect.all(RenderableModel::class.java).exclude(Culled::class.java)), SubRenderer, Resizing {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var mRenderableModel: ComponentMapper<RenderableModel>
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var assets: Assets
    @Wire lateinit var physics: Physics

    val modelBatch = ModelBatch()
    val environment = Environment()

    var instances = com.badlogic.gdx.utils.Array<ModelInstance>()
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

    override fun render() {
        camController.update()
        modelBatch.begin(currentCamera)
        modelBatch.render<ModelInstance>(instances, environment)
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

    }

    override fun resize(width: Int, height: Int) {
        debugFrustumModel.dispose()
        // lets hope whatever is using the cam will position it properly
        val oldCombined = camera.combined.cpy()
        camera.position.x = 0f
        camera.position.y = 0f
        camera.position.z = 10f
        camera.lookAt(0f, 0f, 0f)
        camera.update()
        debugFrustumModel = createFrustumModel(camera)
        debugFrustumInstance = ModelInstance(debugFrustumModel)
        camera.combined.set(oldCombined)
    }
}
