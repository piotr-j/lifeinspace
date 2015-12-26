package io.piotrjastrzebski.lis.game.processors

import com.artemis.Aspect
import com.artemis.Entity
import com.artemis.EntitySystem
import io.piotrjastrzebski.lis.game.components.physics.BodyDef

/**
 * Created by PiotrJ on 25/12/15.
 */
class BodyPositionWrap : EntitySystem(Aspect.all(BodyDef::class.java)) {

    override fun initialize() {

    }

    override fun inserted(e: Entity?) {

    }

    override fun removed(e: Entity?) {

    }
    
    override fun processSystem() { }
}
