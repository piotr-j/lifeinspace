package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import io.piotrjastrzebski.lis.INV_SCALE
import io.piotrjastrzebski.lis.game.processors.*
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
    val tileVerts = floatArrayOf(
            0f, .5f,                // 0
            1f, 0f,                 // 1
            2f, .5f,                // 2
            1f, 1f                  // 3
    )
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
    val tile = Polygon(tileVerts)
    var mapWidth = 0
    var mapHeight = 0
    var layers = Array<Pair<TiledMapTileLayer, Float>>()

    override fun initialize() {
        keybinds.register(Input.Keys.F11, {toggle()}, {false})
        isEnabled = false
        val mapLayers = assets.map.layers
        fun getLayer(name: String): Pair<TiledMapTileLayer, Float> {
            val layer = mapLayers.get(name) as TiledMapTileLayer
            val xOffsetRaw:String? = layer.properties.get("yOffset", null, String::class.java)
            val yOffset = (xOffsetRaw?.toFloat()?: 0f) * INV_SCALE
            return layer to yOffset
        }
        val ground = getLayer("layer0")
        mapWidth = ground.first.width
        mapHeight = ground.first.height
        layers.add(ground)
        layers.add(getLayer("layer1"))
        layers.add(getLayer("layer2"))
        layers.add(getLayer("layer3"))
        // we want need to check top layers first
        layers.reverse()
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
        val wrapper = getCellAt(cs.x, cs.y) ?: return
        drawFancyTile(wrapper.x, wrapper.y, wrapper.elevation)
    }
    /** x, y offsets from center tile to corner tiles*/
    val tileOffsets = intArrayOf(0, 0, 0, -1, 1, -1, 0, 1, 1, 1)

    val cellWrapper = CellWrapper()

    public fun getCellAt(qx:Float, qy:Float): CellWrapper? {
        cellWrapper.reset()
        for ((layer, offset) in layers) {
            var csx = if (qx < 0) qx - 2 else qx
            var csy = if (qy < 0) qy - 1 else qy
            val hOffset = offset + tileHeight
            // we offset the mouse pos down, so it matches the actual map tile position
            csy -= hOffset
            val tx = csx.toInt() / 2
            val ty = csy.toInt() * 2
            for (id in 0..tileOffsets.lastIndex step 2) {
                val x = tx + tileOffsets[id]
                val y = ty + tileOffsets[id + 1]
                val cell = checkTile(x, y, hOffset, layer) ?: continue

                cellWrapper.x = x
                cellWrapper.y = y
                cellWrapper.elevation = hOffset
                cellWrapper.cell = cell
                cellWrapper.layer = layer
                return cellWrapper
            }
        }
        return null
    }

    private fun drawFancyTile(tx: Int, ty: Int, hOffset: Float) {
        renderer.begin(ShapeRenderer.ShapeType.Filled)
//        renderer.color = Color.MAGENTA
//        renderer.color.a = .75f
        // tile as far as map is concerned, base tiles have height of 16 pixels
//        renderTile(tx, ty, -tileHeight)
        renderer.color = Color.GREEN
        renderer.color.a = .75f
        renderTile(tx, ty, hOffset -tileHeight)
        renderer.end()
        renderer.color = Color.RED
        renderer.begin(ShapeRenderer.ShapeType.Line)
        renderTilePoly(tx, ty, hOffset -tileHeight)
        renderer.end()
    }

    val tmpPos = Vector2()
    private fun calculateTilePos(x:Int, y:Int, hOffset:Float = 0f): Vector2 {
        val wy = (y % mapHeight + mapHeight) % mapHeight
        val offsetX = if ((wy % 2 == 1)) 1f else 0f
        val tx = x * 2f - offsetX
        // layer offset * INV_SCALE
        val ty = y * 0.5f
        return tmpPos.set(tx, ty + hOffset + tileHeight)
    }

    private fun checkTile(x: Int, y: Int, hOffset: Float = 0f, layer: TiledMapTileLayer): TiledMapTileLayer.Cell? {
        val wx = (x % mapWidth + mapWidth) % mapWidth
        val wy = (y % mapHeight + mapHeight) % mapHeight
        val cell = layer.getCell(wx, wy) ?: return null
        // TODO do something with the cell
        val pos = calculateTilePos(x, y, hOffset -tileHeight)
        tile.setPosition(pos.x, pos.y)
        return if (tile.contains(cs.x, cs.y)) cell else null
    }

    private fun renderTile(x:Int, y:Int, hOffset:Float = 0f) {
        val pos = calculateTilePos(x, y, hOffset)
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
        renderer.polygon(tile.transformedVertices)
    }

    public class CellWrapper() {
        var x = -1
        var y = -1
        var elevation = 0f
        var layer: TiledMapTileLayer? = null
        var cell: TiledMapTileLayer.Cell? = null
        fun reset() {
            x = -1
            y = -1
            layer = null
            cell = null
        }
    }

    override fun processSystem() { }
}
