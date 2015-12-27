package io.piotrjastrzebski.lis.game.processors

import com.artemis.*
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import io.piotrjastrzebski.lis.game.components.CameraFollower
import io.piotrjastrzebski.lis.game.components.Dynamic
import io.piotrjastrzebski.lis.game.components.Player
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.BodyDef
import io.piotrjastrzebski.lis.game.components.physics.CircleDef
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
//        trans.xy.set(32f, 24f)
        trans.bounds.setSize(.75f, .75f)
        val bodyDef = e.create<BodyDef>()
        bodyDef.def.type = BodyType.DynamicBody
        bodyDef.def.linearDamping = 10f
        bodyDef.friction = .85f
        bodyDef.restitution = .25f
        bodyDef.density = 1f

        val circleDef = e.create<CircleDef>()
        circleDef.radius = .375f

        e.create<CameraFollower>()
        e.create<Dynamic>()
    }

    override fun processSystem() {

    }

    override fun removed(entityId: Int) {
        // queue next spawn or whatever
    }
}

