package io.piotrjastrzebski.lis.game.processors.physics

import com.artemis.BaseSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.utils.ObjectSet

/**
 * Created by PiotrJ on 25/12/15.
 */
class Physics : BaseSystem() {
    val collisionConfig: btCollisionConfiguration;
    val dispatcher: btDispatcher
    val broadPhase: btDbvtBroadphase
    val constraintSolver: btSequentialImpulseConstraintSolver
    val btWorld: btDiscreteDynamicsWorld
    private val contactListener: BulletContactListener

    init {
        collisionConfig = btDefaultCollisionConfiguration()
        dispatcher = btCollisionDispatcher(collisionConfig)
        broadPhase = btDbvtBroadphase()
        constraintSolver = btSequentialImpulseConstraintSolver()
        btWorld = btDiscreteDynamicsWorld(dispatcher, broadPhase, constraintSolver, collisionConfig)
        btWorld.gravity = Vector3(0f, 0f, -3f)
        contactListener = BulletContactListener()
    }

    public fun addRigidBody(body: btRigidBody) {
        btWorld.addRigidBody(body)
    }

    override fun processSystem() {
        val dt = Math.min(1f / 30f, world.delta);
        btWorld.stepSimulation(dt, 5, 1f/60f)

    }

    override fun dispose() {
        // TODO nuke the bodies
        btWorld.dispose()
        constraintSolver.dispose()
        broadPhase.dispose()
        dispatcher.dispose()
        collisionConfig.dispose()
    }
}
