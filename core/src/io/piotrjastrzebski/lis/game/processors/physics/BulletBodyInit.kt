package io.piotrjastrzebski.lis.game.processors.physics

import com.artemis.*
import com.artemis.annotations.Wire
import io.piotrjastrzebski.lis.game.components.physics.BulletBody
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.BulletBodyDef

/**
 * Created by PiotrJ on 25/12/15.
 */
class BulletBodyInit : BaseEntitySystem(Aspect.all(BulletBodyDef::class.java, Transform::class.java)) {
    @Wire lateinit var physics: Physics
    @Wire lateinit var mTransform: ComponentMapper<Transform>
    @Wire lateinit var mBulletBody: ComponentMapper<BulletBody>
    @Wire lateinit var mBulletBodyDef: ComponentMapper<BulletBodyDef>


    override fun initialize() {
        isEnabled = false
    }

    override fun inserted(e: Int) {

    }

    override fun removed(e: Int) {

    }

    override fun processSystem() { }
}
