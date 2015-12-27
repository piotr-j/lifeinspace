package io.piotrjastrzebski.lis.game.processors

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input.Keys
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import io.piotrjastrzebski.lis.game.components.Player
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.Body
import io.piotrjastrzebski.lis.game.minusAssign
import io.piotrjastrzebski.lis.game.processors.physics.FixedUpdatable
import io.piotrjastrzebski.lis.game.processors.physics.Physics
import io.piotrjastrzebski.lis.game.timesAssign
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class PlayerController : IteratingSystem(Aspect.all(Player::class.java, Transform::class.java, Body::class.java)), FixedUpdatable {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var mTransform: ComponentMapper<Transform>
    @Wire lateinit var mBody: ComponentMapper<Body>
    @Wire lateinit var physics: Physics

    val moveKeys = intArrayOf(
            Keys.LEFT, Keys.RIGHT, Keys.UP, Keys.DOWN,
            Keys.A, Keys.D, Keys.W, Keys.S, Keys.SHIFT_LEFT, Keys.SHIFT_RIGHT);
    val cbDown: (Int) -> Boolean = { keyDown(it)}
    val cbUp: (Int) -> Boolean = { keyUp(it)}
    override fun initialize() {
        keybinds.register(moveKeys, cbDown, cbUp)
        keybinds.register(Keys.F1, {toggle()}, {false})
        physics.register(this)
//        isEnabled = false
    }

    val tmp = Vector2()
    override fun process(entityId: Int) {
        var scale = if (shift > 0) 25f else 5f
        tmp.setZero()
        if (moveX > 0) tmp.x = scale
        if (moveX < 0) tmp.x = -scale
        if (moveY > 0) tmp.y = scale
        if (moveY < 0) tmp.y = -scale
        // limit so we don't move faster moving diagonally
        tmp.limit(scale)
        val trans = mTransform.get(entityId)
        val body = mBody.get(entityId).body!!
        trans.xy.set(body.position).sub(trans.bounds.width/2f, trans.bounds.height/2f)
    }

    override fun fixedUpdate() {
        if (tmp.isZero) return
        val ids = getSubscription().entities
        for(i in 0..ids.size()) {
            val entityId = ids.get(i)
            val body = mBody.get(entityId).body!!
            tmp -= body.linearVelocity
            tmp *= body.mass
            body.applyLinearImpulse(tmp, body.worldCenter, true)
        }
    }

    private fun toggle(): Boolean {
        isEnabled = !isEnabled
        // not perfect, quite possible to get invalid values if this gets reset while buttons are pressed
        shift = 0
        moveX = 0
        moveY = 0
        if (isEnabled) {
            keybinds.register(moveKeys, cbDown, cbUp)
        } else {
            keybinds.deregister(moveKeys, cbDown, cbUp)
        }
        Gdx.app.log("PlayerMove", "IsEnabled: $isEnabled")
        return false
    }

    var shift = 0
    var moveX = 0
    var moveY = 0
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
