package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.IntMap
import io.piotrjastrzebski.lis.game.components.ModelDef
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.BulletBodyDef
import io.piotrjastrzebski.lis.game.createAndEdit
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.game.create

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
        val v3 = Vector3()
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

                    val edit = world.createAndEdit()
                    val trans = edit.create<Transform>()
                    v3.set(x*TILE_SIZE + offsetX, y*TILE_SIZE/2, zOffset)
                    trans.transform.setToTranslation(v3)
                    trans.transform.rotate(Vector3.Z, tileData.dir - 45)

                    val def = edit.create<ModelDef>()
                    def.model = "tiles"
                    def.nodeId = tileData.nodeId

                    val bulletDef = edit.create<BulletBodyDef>()
                    bulletDef.mass = 0f
                    bulletDef.model = "tiles"
                    bulletDef.nodeId = tileData.nodeId //+"-col"

//                    modelRenderer.createTile(tileData.nodeId, v3, tileData.dir - 45)
                }
//                return
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
