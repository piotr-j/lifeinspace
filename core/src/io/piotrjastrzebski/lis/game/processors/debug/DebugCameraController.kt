package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.OrthographicCamera
import io.piotrjastrzebski.lis.game.processors.KeyBindings
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class DebugCameraController : BaseSystem() {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var keybinds: KeyBindings
    val moveKeys = intArrayOf(
            Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.DOWN,
            Keys.A, Keys.D, Keys.W, Keys.S, Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT);
    val cbDown: (Int) -> Boolean = { keyDown(it)}
    val cbUp: (Int) -> Boolean = { keyUp(it)}
    override fun initialize() {
//        keybinds.register(moveKeys, cbDown, cbUp)
        keybinds.register(this, Keys.F1, {toggle()}, {false})
        isEnabled = false
    }

    private fun toggle(): Boolean {
        isEnabled = !isEnabled
        // not perfect, quite possible to get invalid values if this gets reset while buttons are pressed
        shift = 0
        moveX = 0
        moveY = 0
        if (isEnabled) {
            keybinds.register(this, moveKeys, cbDown, cbUp)
        } else {
            keybinds.unregister(this, moveKeys)
        }
        Gdx.app.log("DebugCameraMove", "IsEnabled: $isEnabled")
        return false
    }

    var shift = 0
    var moveX = 0
    var moveY = 0

    override fun processSystem() {
        var scale = if (shift > 0) 25f else 5f
        scale *= world.delta
        if (moveX > 0) camera.position.x += scale
        if (moveX < 0) camera.position.x -= scale
        if (moveY > 0) camera.position.y += scale
        if (moveY < 0) camera.position.y -= scale
        camera.update()
    }

    fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Keys.LEFT, Keys.A -> moveX--
            Keys.RIGHT, Keys.D -> moveX++
            Keys.UP, Keys.W -> moveY++
            Keys.DOWN, Keys.S -> moveY--
            Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT -> shift++
        }
        return isEnabled
    }

    fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Keys.LEFT, Keys.A -> moveX++
            Keys.RIGHT, Keys.D -> moveX--
            Keys.UP, Keys.W -> moveY--
            Keys.DOWN, Keys.S -> moveY++
            Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT -> shift--
        }
        return isEnabled
    }
}
