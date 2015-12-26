package io.piotrjastrzebski.lis.game.components

import com.artemis.PooledComponent
import com.artemis.annotations.Transient
import io.piotrjastrzebski.lis.game.components.physics.CircleDef

/**
 * Created by PiotrJ on 22/12/15.
 */
// TODO check if annotation works
@Transient
class BoxDef() : PooledComponent() {
    var width = 0f
    var height = 0f


    fun size (width: Float, height: Float): BoxDef {
        this.width = width
        this.height = height
        return this
    }

    override fun reset() {
        width = 0f
        height = 0f
    }
}
