package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import io.piotrjastrzebski.lis.game.processors.KeyBindings
import io.piotrjastrzebski.lis.game.processors.Physics
import io.piotrjastrzebski.lis.game.processors.SubRenderer
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by PiotrJ on 25/12/15.
 */
class DebugBox2dRenderer : BaseSystem(), SubRenderer {
    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @field:Wire lateinit var keybinds: KeyBindings
    @field:Wire lateinit var physics: Physics
    var renderer = Box2DDebugRenderer()
    var render = false
    override fun initialize() {
        keybinds.register(Input.Keys.F5, {toggle()}, {false})
//        isEnabled = false
    }

    private fun toggle(): Boolean {
        render = !render
        Gdx.app.log("DebugBox2dRenderer", "Render: $render")
        return true
    }

    override fun render() {
        if (!render) return
        renderer.render(physics.box2d, camera.combined)
    }

    override fun processSystem() { }
}
