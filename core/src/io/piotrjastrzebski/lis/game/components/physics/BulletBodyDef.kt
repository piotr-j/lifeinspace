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
class BulletBodyDef() : PooledComponent() {
    public enum class Shape {BOX, SPHERE, MESH}
    var type = Shape.BOX

    public var mass = 0f
    // collision model and node
    public lateinit var model:String
    public lateinit var nodeId:String

    public var userValue = 0
    public var contactCallbackFlag = 1
    public var contactCallbackFilter = 0

    override fun reset() {
        mass = 0f
        userValue = 0
        contactCallbackFlag = 1
        contactCallbackFilter = 0
    }
}
