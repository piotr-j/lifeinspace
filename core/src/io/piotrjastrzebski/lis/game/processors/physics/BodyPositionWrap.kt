package io.piotrjastrzebski.lis.game.processors.physics

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import io.piotrjastrzebski.lis.game.components.Dynamic
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.BulletBody

/**
 * Created by PiotrJ on 25/12/15.
 */
class BodyPositionWrap : IteratingSystem(Aspect.all(BulletBody::class.java, Transform::class.java, Dynamic::class.java)) {

    @Wire lateinit var mTransform: ComponentMapper<Transform>

    override fun initialize() {

    }

    override fun process(entityId: Int) {

    }
}
