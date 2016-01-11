package io.piotrjastrzebski.lis.game.components

import com.artemis.PooledComponent

/**
 * Created by PiotrJ on 11/01/16.
 */
class ModelDef : PooledComponent() {
    public lateinit var model: String
    public lateinit var nodeId: String

    override fun reset() {

    }
}
