package io.piotrjastrzebski.lis.game.components.physics

import com.artemis.PooledComponent
import com.artemis.annotations.Transient
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import io.piotrjastrzebski.lis.game.processors.physics.BulletMotionState

/**
 * Created by PiotrJ on 22/12/15.
 */
// TODO check if annotation works
@Transient
class BulletBody() : PooledComponent() {
    public var body : btRigidBody? = null
    public var ms : BulletMotionState? = null

    override fun reset() {
        body = null
    }
}
