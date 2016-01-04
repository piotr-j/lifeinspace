package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.IntMap
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by PiotrJ on 22/12/15.
 */
class MapParser() : BaseSystem() {
    @Wire lateinit var assets: Assets
    @Wire lateinit var renderer: ModelRenderer

    override fun initialize() {
        val tiles = assets.tilesModel

        // fixme this map is shit
        val tileIdToNameRot = IntMap<Pair<String, Float>>()
        val tileSet = assets.map.tileSets.getTileSet(0)
        fun dirToRot(dir:String):Float {
            when (dir) {
                "se" -> return 0f
                "ne" -> return 90f
                "nw" -> return 180f
                "sw" -> return 270f
                else -> return 0f
            }
        }

        for (id in 0..tileSet.size() -1) {
            val props = tileSet.getTile(id)?.properties ?: continue
            if (props.containsKey("id")) {
                val tileId = props.get("id", String::class.java)
                // TODO need to make sure which one is default rot
                val tileDir = props.get("dir", "sw", String::class.java)
                tileIdToNameRot.put(id, tileId to dirToRot(tileDir))
                Gdx.app.log("", "${tileIdToNameRot.get(id)}")
            }
        }

        val layers = assets.map.layers
        var zOffset = 1f
        val unit = Math.sqrt(2.0).toFloat()
        for (layer in layers) {
            layer as TiledMapTileLayer
            for (x in 0..layer.width) {
                for (y in 0..layer.height) {
                    val offsetX = if ((y % 2 == 1)) unit/2 else 0f
                    val cell: TiledMapTileLayer.Cell? = layer.getCell(x, y) ?: continue
                    val tile: TiledMapTile? = cell!!.tile ?: continue
                    // TODO get id from tile
                    if (!tileIdToNameRot.containsKey(tile!!.id)) continue
                    val pair = tileIdToNameRot.get(tile.id)
                    val id = pair.first
                    //                val id = "tile"
                    val instance = ModelInstance(tiles, id)
                    if (instance.nodes.size == 0) continue
                    instance.transform.rotate(Vector3.X, 90f)
                    instance.transform.rotate(Vector3.Y, pair.second - 45)
                    instance.transform.setTranslation(x*unit - offsetX, y*unit/2, zOffset)
                    instance.calculateTransforms()
                    // todo create entity
                    renderer.instances.add(instance)
                }
            }
            zOffset+=.25f
        }
        isEnabled = false
    }

    override fun processSystem() {}
}
