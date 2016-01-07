package io.piotrjastrzebski.lis.game.processors.physics

import com.artemis.Aspect
import com.artemis.BaseEntitySystem
import com.artemis.utils.IntBag
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.BodyDef
import kotlin.collections.mapOf

/**
 * Created by EvilEntity on 06/01/2016.
 */
class BulletBodyInit : BaseEntitySystem(Aspect.all(BodyDef::class.java, Transform::class.java)){
    private val localInertia = Vector3()

    override fun initialize() {
        isEnabled = false
    }

    override fun inserted(entities: IntBag?) {

    }

    override fun removed(entities: IntBag?) {

    }

    override fun processSystem() { }
}
