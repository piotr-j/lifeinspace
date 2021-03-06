package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.IntMap
import io.piotrjastrzebski.lis.utils.InputHandler

/**
 * Created by PiotrJ on 24/12/15.
 */
class KeyBindings : BaseSystem(), InputHandler {
    val MOD_CTRL = 1 shl 10
    val MOD_ALT = 1 shl 11
    val MOD_SHIFT = 1 shl 12

    private var modifiers = 0

    override fun initialize() { }

    internal inner class Mapping(val owner:Any, val action: Function1<Int, Boolean>)

    private val keyToFunDown = IntMap<Array<Mapping>>()
    private val keyToFunUp = IntMap<Array<Mapping>>()

    fun register(owner: Any, keys: IntArray, cbDown: (key: Int) -> Boolean, cbUp: (key: Int) -> Boolean) {
        // todo priority?
        for (key in keys) {
            register(owner, key, cbDown, cbUp)
        }
    }

    fun register(owner: Any, key: Int, cbDown: (key: Int) -> Boolean, cbUp: (key: Int) -> Boolean) {
        var downs: Array<Mapping>? = keyToFunDown.get(key, null)
        if (downs == null) {
            downs = Array()
            keyToFunDown.put(key, downs)
        }
        downs.add(Mapping(owner, cbDown))
        var ups: Array<Mapping>? = keyToFunUp.get(key, null)
        if (ups == null) {
            ups = Array()
            keyToFunUp.put(key, ups)
        }
        ups.add(Mapping(owner, cbUp))
    }

    fun unregister(owner: Any, keys: IntArray) {
        for (key in keys) {
            unregister(owner, key)
        }
    }

    fun unregister(owner: Any, key: Int) {
        var downs: Array<Mapping>? = keyToFunDown.get(key, null)
        if (downs != null) {
            val it = downs.iterator()
            while(it.hasNext()) {
                val next = it.next()
                if (next.owner == owner) {
                    it.remove()
                    break
                }
            }
        }
        var ups: Array<Mapping>? = keyToFunUp.get(key, null)
        if (ups != null) {
            val it = ups.iterator()
            while(it.hasNext()) {
                val next = it.next()
                if (next.owner == owner) {
                    it.remove()
                    break
                }
            }
        }
    }

    override fun processSystem() {
        // do nothing?
    }

    override fun keyDown(keycode: Int): Boolean {
        when (keycode) {
            Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT -> modifiers = modifiers or MOD_CTRL
            Keys.ALT_LEFT, Keys.ALT_RIGHT -> modifiers = modifiers or MOD_ALT
            Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT -> modifiers = modifiers or MOD_SHIFT
        }
//        return modKeyDown(modifiers or keycode)
        return modKeyDown(keycode)
    }

    private fun modKeyDown(keyCode: Int): Boolean {
        if (keyToFunDown.containsKey(keyCode)) {
            for (mapping in keyToFunDown.get(keyCode)) {
                if (mapping.action.invoke(keyCode)) break
            }
        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        when (keycode) {
            Keys.CONTROL_LEFT, Keys.CONTROL_RIGHT -> modifiers = modifiers and MOD_CTRL.inv()
            Keys.ALT_LEFT, Keys.ALT_RIGHT -> modifiers = modifiers and MOD_ALT.inc()
            Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT -> modifiers = modifiers and MOD_SHIFT.inv()
        }
//        return modKeyUp(modifiers or keycode)
        return modKeyUp(keycode)
    }

    private fun modKeyUp(keyCode: Int): Boolean {
        if (keyToFunUp.containsKey(keyCode)) {
            for (mapping in keyToFunUp.get(keyCode)) {
                if (mapping.action.invoke(keyCode)) break
            }
        }
        return false
    }

    private fun translateKeyCode(keyCode: Int): Int {
        // we need to use modifiers as these keys are always reported the same regardless of shift statuus
        when (keyCode) {
            Keys.PLUS -> return Keys.EQUALS
            Keys.AT -> return Keys.NUM_2
            Keys.POUND -> return Keys.NUM_3
            Keys.STAR -> return Keys.NUM_8
            Keys.COLON -> return Keys.SEMICOLON
        }
        return keyCode
    }

    fun hasMod(mods: Int, mod: Int) = mods and mod === 0
    fun getKeycode(meta: Int) = meta and 0xFFFF
}
