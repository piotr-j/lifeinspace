package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.IntMap
import io.piotrjastrzebski.lis.game.components.RenderableModel
import io.piotrjastrzebski.lis.game.createAndEdit
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by PiotrJ on 22/12/15.
 */
val TILE_HEIGHT = .25f
/** tiles are 1x1, but rotated 45 deg */
val TILE_SIZE = Math.sqrt(2.0).toFloat()
class MapParser() : BaseSystem() {
    @Wire lateinit var assets: Assets

    val tileIdToTileData = IntMap<TileData>()
    override fun initialize() {
        val tiles = assets.tilesModel

        // we only have one tile set
        val tileSet = assets.map.tileSets.getTileSet(0)
        // find direction and model id for each tile in the sets
        for (id in 0..tileSet.size() -1) {
            val props = tileSet.getTile(id)?.properties ?: continue
            if (props.containsKey("id")) {
                val tileId = props.get("id", String::class.java)
                // se is the default, no rotation
                val tileDir = props.get("dir", "se", String::class.java)
                tileIdToTileData.put(id, TileData(tileId, dirToRot(tileDir)))
            }
        }

        var zOffset = 0f
        for (layer in assets.map.layers) {
            layer as TiledMapTileLayer
            for (y in 0..layer.height) {
                val offsetX = if ((y % 2 == 1)) -TILE_SIZE/2 else 0f
                for (x in 0..layer.width) {
                    // offset so every other row is shifter to the left
                    val tile: TiledMapTile = layer.getCell(x, y)?.tile ?: continue
                    if (!tileIdToTileData.containsKey(tile.id)) continue
                    val tileData = tileIdToTileData.get(tile.id)
                    // TODO is this model stuff correct?
                    val instance = ModelInstance(tiles, tileData.nodeId)
                    if (instance.nodes.size == 0) continue
                    // face the camera
                    instance.transform.rotate(Vector3.X, 90f)
                    // rotate left so its on the corner+
                    instance.transform.rotate(Vector3.Y, tileData.dir - 45)
                    // tiles are spaced at size in the x, but size/2 on the y, so they overlap
                    instance.transform.setTranslation(x*TILE_SIZE + offsetX, y*TILE_SIZE/2, zOffset)
                    instance.calculateTransforms()
                    // TODO add some other tile data
                    // lets play with bullet in here, then extract it
                    // get col shape from models, col-full, etc
                    // col-corner-in-a, b for inner corner
                    val edit = world.createAndEdit()
                    edit.create(RenderableModel::class.java).instance = instance
                }
            }
            zOffset += TILE_HEIGHT
        }
        // no need to process
        isEnabled = false
    }

    private fun dirToRot(dir:String):Float {
        when (dir) {
            "se" -> return 0f
            "ne" -> return 90f
            "nw" -> return 180f
            "sw" -> return 270f
            else -> return 0f
        }
    }

    data class TileData(val nodeId:String, val dir:Float)

    override fun processSystem() {}
}
