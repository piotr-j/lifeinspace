package io.piotrjastrzebski.lis.game.components.physics

import com.artemis.PooledComponent
import com.artemis.annotations.Transient

/**
 * Created by PiotrJ on 22/12/15.
 */
// TODO check if annotation works
@Transient
class Body() : PooledComponent() {
    public var body: com.badlogic.gdx.physics.box2d.Body? = null
    override fun reset() {
        body = null
    }
}
