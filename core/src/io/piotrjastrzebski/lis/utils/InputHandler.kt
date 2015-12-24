package io.piotrjastrzebski.lis.utils

import com.badlogic.gdx.InputProcessor

/**
 * Marks System as input handling
 *
 * Created by PiotrJ on 22/12/15.
 */
interface InputHandler : InputProcessor {
    override fun scrolled(amount: Int) = false
    override fun mouseMoved(screenX: Int, screenY: Int) = false
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
    override fun keyDown(keycode: Int) = false
    override fun keyUp(keycode: Int) = false
    override fun keyTyped(character: Char) = false
}
