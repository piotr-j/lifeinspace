package io.piotrjastrzebski.lis.game.processors.physics

import com.badlogic.gdx.physics.bullet.collision.ContactListener

/**
 * Created by EvilEntity on 06/01/2016.
 */
class BulletContactListener : ContactListener() {
    // note this is magic with static stuff, automagically added to bullet
    // note only one method can be overridden, jni magic
    override fun onContactAdded(userValue0: Int, partId0: Int, index0: Int, match0: Boolean,
                                userValue1: Int, partId1: Int, index1: Int, match1: Boolean): Boolean {
        // TODO add stuff here? do we even want this as a separate class?
        return true
    }
}
