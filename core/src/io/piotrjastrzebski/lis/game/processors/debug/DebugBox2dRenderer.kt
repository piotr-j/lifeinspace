package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import io.piotrjastrzebski.lis.game.processors.KeyBindings
import io.piotrjastrzebski.lis.game.processors.MAP_HEIGHT
import io.piotrjastrzebski.lis.game.processors.MAP_WIDTH
import io.piotrjastrzebski.lis.game.processors.physics.Physics
import io.piotrjastrzebski.lis.game.processors.SubRenderer
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by PiotrJ on 25/12/15.
 */
class DebugBox2dRenderer : BaseSystem(), SubRenderer {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var physics: Physics
    var renderer = Box2DDebugRenderer()
    var render = true
    override fun initialize() {
        keybinds.register(this, Input.Keys.F5, {toggle()}, {false})
        isEnabled = false
    }

    private fun toggle(): Boolean {
        render = !render
        Gdx.app.log("DebugBox2dRenderer", "Render: $render")
        return true
    }

    override fun render() {
        if (!render) return
        render(0f, 0f)
        render(MAP_WIDTH, 0f)
        render(0f, MAP_HEIGHT)
        render(MAP_WIDTH, MAP_HEIGHT)
        camera.update()
    }

    private fun render(width: Float, height: Float) {
        camera.position.x += width
        camera.position.y += height
        camera.update()
        renderer.render(physics.box2d, camera.combined)
        camera.position.x -= width
        camera.position.y -= height
    }

    override fun processSystem() { }
}
