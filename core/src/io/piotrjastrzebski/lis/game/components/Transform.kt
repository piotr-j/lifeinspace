package io.piotrjastrzebski.lis.game.components

import com.artemis.PooledComponent
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

/**
 * Created by PiotrJ on 22/12/15.
 */

class Transform() : PooledComponent() {
    var xy = Vector2()
    var angle = 0f
    var scale = Vector2()
    var bounds = Rectangle()

    fun xy(x: Float, y: Float): Transform {
        xy.set(x ,y)
        return this
    }

    fun angle(angle: Float): Transform {
        this.angle = angle
        return this
    }

    fun scale(x: Float, y: Float): Transform {
        scale.set(x ,y)
        return this
    }

    fun size(width: Float, height: Float): Transform {
        bounds.set(0f, 0f, width, height)
        return this
    }

    override fun reset() {
        xy.setZero()
        angle = 0f
        bounds.x = 0f
        bounds.y = 0f
        bounds.width = 0f
        bounds.height = 0f
    }
}
