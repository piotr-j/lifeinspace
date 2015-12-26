package io.piotrjastrzebski.lis.game

import com.artemis.Component
import com.artemis.EntityEdit
import com.artemis.World
import com.badlogic.gdx.math.Vector2

/**
 * Created by EvilEntity on 21/12/2015.
 */
/**
 * Now that's a mouthful!
 *
 * Usage:
 * edit.create<Transform>()
 * will be translated to
 * edit.create(Transform::class.java)
 */
inline fun <reified T: Component> EntityEdit.create(): T = this.create(T::class.java)

@Suppress("NOTHING_TO_INLINE")
inline fun World.createAndEdit(): EntityEdit = this.createEntity().edit()

operator fun Vector2.plusAssign(other: Vector2?) {
    this.add(other)
}

operator fun Vector2.minusAssign(other: Vector2?) {
    this.sub(other)
}

operator fun Vector2.timesAssign(scalar: Float) {
    this.scl(scalar)
}
