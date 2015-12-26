package io.piotrjastrzebski.lis.game.components.physics

import com.artemis.PooledComponent
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
/**
 * Created by PiotrJ on 22/12/15.
 */

class BodyDef() : PooledComponent() {
    var def = com.badlogic.gdx.physics.box2d.BodyDef()

    var restitution: Float = 0f
    var friction: Float = 0f
    var density: Float = 1f
    var categoryBits: Short = 1
    var groupIndex: Short = 0
    var maskBits: Short = -1

    fun type(type: BodyType): BodyDef {
        def.type = type
        return this
    }


    fun position(x: Float, y: Float): BodyDef {
        def.position.set(x, y)
        return this
    }

    fun position(position: Vector2): BodyDef {
        def.position.set(position)
        return this
    }

    fun rotation(degrees: Float): BodyDef {
        def.angle = degrees * MathUtils.degreesToRadians
        return this
    }

    fun angle(radians: Float): BodyDef {
        def.angle = radians
        return this
    }

    override fun reset() {
        def.type = BodyType.StaticBody
        def.position.setZero()
        def.angle = 0f
        def.linearVelocity.setZero()
        def.angularVelocity = 0f
        def.linearDamping = 0f
        def.angularDamping = 0f
        def.bullet = false
        def.fixedRotation = false
        def.allowSleep = true
        def.active = true
        def.awake = true
        def.gravityScale = 1f

        categoryBits = 1
        groupIndex = 0
        maskBits = -1

        restitution = 0f
        friction = 0f
        density = 1f
    }
}
