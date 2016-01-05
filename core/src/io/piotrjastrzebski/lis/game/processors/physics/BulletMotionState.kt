package io.piotrjastrzebski.lis.game.processors.physics

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState

/**
 * Created by EvilEntity on 06/01/2016.
 */
class BulletMotionState : btMotionState() {
    lateinit var transform: Matrix4
    override fun getWorldTransform (worldTrans: Matrix4) {
        worldTrans.set(transform);
    }

    override fun setWorldTransform (worldTrans: Matrix4) {
        transform.set(worldTrans);
    }
}
