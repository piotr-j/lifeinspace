package io.piotrjastrzebski.lis.game.processors.physics

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.BulletBody
import io.piotrjastrzebski.lis.game.components.physics.BulletBodyDef
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by PiotrJ on 25/12/15.
 */
class Physics : BaseEntitySystem(Aspect.all(BulletBodyDef::class.java, Transform::class.java)) {
    @Wire lateinit var mTransform: ComponentMapper<Transform>
    @Wire lateinit var mBulletBody: ComponentMapper<BulletBody>
    @Wire lateinit var mBulletBodyDef: ComponentMapper<BulletBodyDef>
    @Wire lateinit var assets: Assets

    val collisionConfig: btCollisionConfiguration;
    val dispatcher: btDispatcher
//    val broadPhase: btDbvtBroadphase
//    val constraintSolver: btSequentialImpulseConstraintSolver
    val btWorld: btDiscreteDynamicsWorld
//    private val contactListener: BulletContactListener

    private var ghostPairCallback: btGhostPairCallback

    init {
//        collisionConfig = btDefaultCollisionConfiguration()
//        dispatcher = btCollisionDispatcher(collisionConfig)
//        broadPhase = btDbvtBroadphase()
//        constraintSolver = btSequentialImpulseConstraintSolver()
//        btWorld = btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, collisionConfig)
//        btWorld.gravity = Vector3(0f, 0f, -3f)
//        contactListener = BulletContactListener()

        collisionConfig = btDefaultCollisionConfiguration();
        dispatcher = btCollisionDispatcher(collisionConfig);
        val sweep = btAxisSweep3(Vector3(-1000f, -1000f, -1000f), Vector3(1000f, 1000f, 1000f));
        val solver = btSequentialImpulseConstraintSolver();
        btWorld = btDiscreteDynamicsWorld(dispatcher, sweep, solver, collisionConfig);
        ghostPairCallback = btGhostPairCallback();
        sweep.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);
    }

    val tmpLocalInertia = Vector3()
    val tmpMeshParts = Array<MeshPart>()
    override fun inserted(entityId: Int) {
        val trans = mTransform.get(entityId)
        val def = mBulletBodyDef.get(entityId)

        val info = getInfo(def.model, def.nodeId, def.mass)
        if (info == null) {
            Gdx.app.error("", "No info! ${def.model} ${def.nodeId}")
            return
        }
        val bodyComp = mBulletBody.create(entityId)
        val ms = BulletMotionState()
        ms.transform = trans.transform
        val body = btRigidBody(info.constructionInfo)
        body.motionState = ms
        body.proceedToTransform(trans.transform)
//        body.contactCallbackFlag = def.contactCallbackFlag
//        body.contactCallbackFilter = def.contactCallbackFilter
//        body.userData = def.userValue
        addRigidBody(body)

        bodyComp.body = body
        bodyComp.ms = ms

    }

    private val tmpKey = InfoKey("", "", 0f)
    private val infoMap = ObjectMap<InfoKey, InfoData>()
    private fun getInfo(model: String, nodeId: String, mass: Float): InfoData? {
        tmpKey.model = model
        tmpKey.nodeId = nodeId
        tmpKey.mass = mass
        var infoData = infoMap.get(tmpKey, null)
        if (infoData == null) {
            Gdx.app.log("", "Create info for $model, $nodeId, $mass")
            val modelData = assets.getModel(model)?: return null
            tmpMeshParts.clear()
            // note this is probably suboptimal
            tmpMeshParts.add(modelData.getNode(nodeId).parts.first().meshPart)
            // could use a shape def, not everything needs to be a mesh
            // most of the tiles could be boxes
            val shape = btBvhTriangleMeshShape(tmpMeshParts)
            tmpLocalInertia.setZero()
            if (mass > 0f) shape.calculateLocalInertia(mass, tmpLocalInertia)
            val cInfo = btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, tmpLocalInertia)
            val key = InfoKey(tmpKey.model, tmpKey.nodeId, tmpKey.mass)
            infoData = InfoData(shape, cInfo)
            infoMap.put(key, infoData)
        }
        return infoData
    }

    private data class InfoData(val shape:btCollisionShape, val constructionInfo: btRigidBody.btRigidBodyConstructionInfo)

    private data class InfoKey(var model: String, var nodeId: String, var mass: Float)

    override fun removed(entityId: Int) {
        val body = mBulletBody.get(entityId)
        body.body?.dispose()
        body.ms?.dispose()
    }

    public fun addRigidBody(body: btRigidBody) {
        btWorld.addRigidBody(body)
    }

    public fun removeRigidBody(body: btRigidBody) {
        btWorld.removeRigidBody(body)
    }

    public fun removeCollisionObject(body: btCollisionObject) {
        btWorld.removeCollisionObject(body)
    }

    private val maxSubSteps = 5
    private val fixedTimeStep = 1f / 60f

    override fun processSystem() {
        val dt = Math.min(1f / 30f, world.delta);
        btWorld.stepSimulation(dt, maxSubSteps, fixedTimeStep)

    }

    override fun dispose() {
        val entities = subscription.entities
        for (id in 0..entities.size() -1) {
            val body = mBulletBody.getSafe(entities.get(id))?: continue
            // if we have a component, the body will be set
            removeRigidBody(body.body!!)
        }
        for (value in infoMap.values()) {
            value.constructionInfo.dispose()
            value.shape.dispose()
        }
        infoMap.clear()
        btWorld.dispose()
//        constraintSolver.dispose()
//        broadPhase.dispose()
        dispatcher.dispose()
        collisionConfig.dispose()
    }
}
