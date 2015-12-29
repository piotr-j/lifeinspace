package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import io.piotrjastrzebski.lis.INV_SCALE
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.processors.*
import io.piotrjastrzebski.lis.game.processors.physics.Physics
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by PiotrJ on 25/12/15.
 */
class DebugTileSelectRenderer : BaseSystem(), SubRenderer {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var cs: CursorPosition
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var renderer: ShapeRenderer
    @Wire lateinit var assets: Assets
    val tileHeight = 16 * INV_SCALE
    var render = true
    val rawVerts = floatArrayOf(
            0f, .5f,                // 0
            1f, 0f,                 // 1
            2f, .5f,                // 2
            1f, 1f,                 // 3
            0f, .5f - tileHeight,   // 4
            1f, 0f - tileHeight,    // 5
            2f, .5f - tileHeight    // 6
    )
    val indices = shortArrayOf(
        // 0, 6
        0, 1, 3,
        1, 2, 3,
        0, 4, 1,
        4, 5, 1,
        1, 5, 6,
        1, 2, 6
    )
    val tile = Polygon(rawVerts)
    var layerWidth = 0
    var layerHeight = 0

    override fun initialize() {
        keybinds.register(Input.Keys.F11, {toggle()}, {false})
        isEnabled = false
        val ground = assets.map.layers.get("ground") as TiledMapTileLayer
        layerWidth = ground.width
        layerHeight = ground.height
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
        findSelected()
    }

    fun findSelected() {
        var csx = if (cs.x < 0) cs.x -2 else cs.x
        var csy = if (cs.y < 0) cs.y -1 else cs.y

        val hOffset = tileHeight * 3
        // we offset the mouse pos down, so it matches the actual map tile position
        csy -= hOffset

        val t1x = csx.toInt()/2
        val t1y = csy.toInt()*2
//        val t2x = t1x
//        val t2y = t1y -1
//        val t3x = t1x +1
//        val t3y = t1y -1
//        val t4x = t1x
//        val t4y = t1y -2
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.YELLOW
        renderer.color.a = .33f
        renderer.rect(t1x * 2f, t1y / 2f + hOffset, 2f, 1f)
        renderer.color = Color.MAGENTA
        renderer.color.a = .75f
        // tile as far as map is concerned, base tiles have height of 16 pixels
        renderTile(t1x, t1y, -tileHeight, false)
        renderer.color = Color.GREEN
        renderer.color.a = .75f
        renderTile(t1x, t1y, hOffset -tileHeight)
        renderer.end()
        renderer.color = Color.RED
        renderer.begin(ShapeRenderer.ShapeType.Line)
        renderTilePoly(t1x, t1y, hOffset -tileHeight)
        renderer.end()
//        renderTile(t2x, t2y, 0f)
//        renderTile(t3x, t3y, 0f)
//        renderTile(t4x, t4y, 0f)
//        renderer.color.a = 1f
//        if (checkTile(t1x, t1y, hOffset)) {
//            renderTile(t1x, t1y, hOffset)
//        }
//        if (checkTile(t2x, t2y, hOffset)) {
//            renderTile(t2x, t2y, hOffset)
//        }
//        if (checkTile(t3x, t3y, hOffset)) {
//            renderTile(t3x, t3y, hOffset)
//        }
//        if (checkTile(t4x, t4y, hOffset)) {
//            renderTile(t4x, t4y, hOffset)
//        }
    }

    val tmpPos = Vector2()
    private fun calculateTilePos(x:Int, y:Int, hOffset:Float = 0f): Vector2 {
        val wy = (y % layerHeight + layerHeight) % layerHeight
        val offsetX = if ((wy % 2 == 1)) 1f else 0f
        val tx = x * 2f - offsetX
        // layer offset * INV_SCALE
        val ty = y * 0.5f
        return tmpPos.set(tx, ty + hOffset + tileHeight)
    }

    private fun checkTile(x: Int, y: Int, hOffset:Float = 0f): Boolean {
        val pos = calculateTilePos(x, y, hOffset)
        tile.setPosition(pos.x, pos.y)
        return tile.contains(cs.x, cs.y)
    }

    private fun renderTile(x:Int, y:Int, hOffset:Float = 0f, depth:Boolean = true) {
        val pos = calculateTilePos(x, y, hOffset)
        tile.setPosition(pos.x, pos.y)
        val count = if (hOffset >= tileHeight) 5 else 1
        for (id in 0..count) {
            val v0 = indices[id * 3] * 2
            val v1 = indices[id * 3 + 1] * 2
            val v2 = indices[id * 3 + 2] * 2
            renderer.triangle(
                    rawVerts[v0] + pos.x, rawVerts[v0 + 1] + pos.y,
                    rawVerts[v1] + pos.x, rawVerts[v1 + 1] + pos.y,
                    rawVerts[v2] + pos.x, rawVerts[v2 + 1] + pos.y
            )
        }
    }

    private fun renderTilePoly(x:Int, y:Int, hOffset:Float = 0f) {
        val pos = calculateTilePos(x, y, hOffset)
        tile.setPosition(pos.x, pos.y)
        renderer.polygon(tile.transformedVertices, 0, 8)
    }

    override fun processSystem() { }
}
