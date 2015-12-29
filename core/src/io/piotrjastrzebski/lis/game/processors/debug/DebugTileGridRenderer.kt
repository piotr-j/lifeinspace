package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import io.piotrjastrzebski.lis.INV_SCALE
import io.piotrjastrzebski.lis.game.processors.*
import io.piotrjastrzebski.lis.game.processors.physics.Physics
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by PiotrJ on 25/12/15.
 */
class DebugTileGridRenderer : BaseSystem(), SubRenderer {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var cs: CursorPosition
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var renderer: ShapeRenderer
    @Wire lateinit var assets: Assets
    var render = false
    val rawVerts = floatArrayOf(0f, .5f, 1f, 0f, 2f, .5f, 1f, 1f)
    val indices = shortArrayOf(0, 1, 3, 1, 2, 3)

    override fun initialize() {
        keybinds.register(Input.Keys.F10, {toggle()}, {false})
        isEnabled = false
    }

    private fun toggle(): Boolean {
        render = !render
        Gdx.app.log("DebugBox2dRenderer", "Render: $render")
        return true
    }

    override fun render() {
        if (!render) return
        Gdx.gl.glEnable(GL20.GL_BLEND)
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
        renderer.projectionMatrix = camera.combined
        renderer.setColor(0f, 1f, 0f, .5f)
        renderer.begin(ShapeRenderer.ShapeType.Line)
        renderGrid(0f, 0f)
        renderGrid(-MAP_WIDTH, 0f)
        renderGrid(0f, -MAP_HEIGHT)
        renderGrid(-MAP_WIDTH, -MAP_HEIGHT)
        renderer.end()
    }

    private fun renderGrid(sx: Float, sy: Float) {
        val ground = assets.map.layers.get("ground") as TiledMapTileLayer
        val layerWidth = ground.width
        val layerHeight = ground.height
//        renderer.rect(0f, 0f, 2f, 1f)
        for (x in 0..layerWidth -1) {
            for (y in 0..layerHeight -1) {
//                if ((x == 0) and (y == 0)) {
//                    renderer.setColor(1f, 0f, 0f, 1f)
//                } else if ((x == 1) and (y == 0)) {
//                    renderer.setColor(0f, 1f, 0f, 1f)
//                } else if ((x == 0) and (y == 1)) {
//                    renderer.setColor(0f, 1f, 1f, 1f)
//                } else if ((x == 1) and (y == 1)) {
//                    renderer.setColor(1f, 0f, 1f, 1f)
//                } else if ((x >= (layerWidth - 1)) and (y >= (layerHeight - 1))) {
//                    renderer.setColor(1f, 0f, 1f, 1f)
//                }  else {
//                    renderer.setColor(x/layerWidth.toFloat(), 0f, 1f-y/layerHeight.toFloat(), .5f)
//                }
                val offsetX = if ((y % 2 == 1)) 1f else 0f
                val tx = x * 2f - offsetX
                // layer offset * INV_SCALE
                val ty = y * 0.5f + 16 * INV_SCALE
//                renderer.rect(tx, ty, 2f, 1f)

                for (id in 0..2) {
                    val v0 = indices[id] * 2
                    val v1 = indices[id + 1] * 2
                    val v2 = indices[id + 2] * 2
                    renderer.triangle(
                            rawVerts[v0] + tx + sx, rawVerts[v0 + 1] + ty + sy,
                            rawVerts[v1] + tx + sx, rawVerts[v1 + 1] + ty + sy,
                            rawVerts[v2] + tx + sx, rawVerts[v2 + 1] + ty + sy
                    )
                }
            }
        }
    }

    override fun processSystem() { }
}
