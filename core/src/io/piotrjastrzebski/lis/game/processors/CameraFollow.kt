package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.OrthographicCamera
import io.piotrjastrzebski.lis.game.components.CameraFollower
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.processors.KeyBindings
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class CameraFollow : IteratingSystem(Aspect.all(Transform::class.java, CameraFollower::class.java)) {
    public val TAG = CameraFollow::class.simpleName

    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @field:Wire lateinit var keybinds: KeyBindings
    @field:Wire lateinit var mCameraFollower: ComponentMapper<CameraFollower>
    @field:Wire lateinit var mTransform: ComponentMapper<Transform>

    override fun initialize() {
        keybinds.register(intArrayOf(Keys.F6), {toggle()}, {false})
    }

    private fun toggle(): Boolean {
        isEnabled = !isEnabled
        Gdx.app.log(TAG, "IsEnabled: $isEnabled")
        return false
    }

    override fun process(entityId: Int) {
        val trans = mTransform.get(entityId)
        camera.position.x = trans.xy.x + trans.bounds.width/2f
        camera.position.y = trans.xy.y + trans.bounds.height/2f
    }
}
