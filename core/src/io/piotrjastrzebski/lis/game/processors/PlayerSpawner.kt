package io.piotrjastrzebski.lis.game.processors

import com.artemis.*
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import io.piotrjastrzebski.lis.game.components.CameraFollower
import io.piotrjastrzebski.lis.game.components.Dynamic
import io.piotrjastrzebski.lis.game.components.Player
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.create
import io.piotrjastrzebski.lis.game.createAndEdit
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class PlayerSpawner : BaseEntitySystem(Aspect.all(Player::class.java, Transform::class.java)) {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var mTransform: ComponentMapper<Transform>
    @Wire lateinit var mPlayer: ComponentMapper<Player>

    override fun initialize() {
//        keybinds.register(moveKeys, cbDown, cbUp)
        // todo some hacks for testing
//        keybinds.register(Keys.F1, {toggle()}, {false})
        isEnabled = false

        spawnPlayer()
    }

    private fun spawnPlayer() {
        val e = world.createAndEdit()
        e.create<Player>()
        val trans = e.create<Transform>()
        // TODO pick proper spawn position
//        trans.xy.set(64f, 32f)
        trans.xy.set(5f, 5f)
        trans.bounds.setSize(.75f, .75f)

        e.create<CameraFollower>()
        e.create<Dynamic>()
    }

    override fun processSystem() {

    }

    override fun removed(entityId: Int) {
        // queue next spawn or whatever
    }
}

