package io.piotrjastrzebski.lis.game.components

import com.artemis.PooledComponent
import com.artemis.annotations.Transient
import com.badlogic.gdx.graphics.g3d.ModelInstance

/**
 * Created by PiotrJ on 22/12/15.
 */
@Transient
class DebugRenderableModel() : PooledComponent() {
    lateinit var instance: ModelInstance

    override fun reset() {

    }
}
