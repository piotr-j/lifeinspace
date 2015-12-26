package io.piotrjastrzebski.lis.game.components.physics

import com.artemis.PooledComponent
import com.artemis.annotations.Transient

/**
 * Created by PiotrJ on 22/12/15.
 */
// TODO check if annotation works
@Transient
class CircleDef() : PooledComponent() {
    var radius = 0f

    fun radius (radius: Float): CircleDef {
        this.radius = radius
        return this
    }

    override fun reset() {
        radius = 0f
    }
}
