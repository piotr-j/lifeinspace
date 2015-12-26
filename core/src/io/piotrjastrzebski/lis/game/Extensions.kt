package io.piotrjastrzebski.lis.game

import com.artemis.Component
import com.artemis.EntityEdit
import com.artemis.World

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
