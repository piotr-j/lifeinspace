package io.piotrjastrzebski.lis.utils

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Batch.*
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile
import kotlin.ranges.downTo
import kotlin.text.toFloat

/**
 * Created by EvilEntity on 27/12/2015.
 */
class WrapIsoStaggeredRenderer(map: TiledMap, unitScale: Float, batch: Batch) : BatchTiledMapRenderer(map, unitScale, batch) {
    override fun beginRender() {
        // NOTE we have external batch, we don't want to change its state
        AnimatedTiledMapTile.updateAnimationBaseTime()
    }

    override fun endRender() {
        // NOTE we have external batch, we don't want to change its state
    }

    override fun renderTileLayer(layer: TiledMapTileLayer) {
        val batchColor = batch.color
        val color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.opacity)

        val layerWidth = layer.width
        val layerHeight = layer.height

        val layerTileWidth = layer.tileWidth * unitScale
        val layerTileHeight = layer.tileHeight * unitScale

        val layerTileWidth50 = layerTileWidth * 0.50f
        val layerTileHeight50 = layerTileHeight * 0.50f

        val minX = (((viewBounds.x - layerTileWidth50) / layerTileWidth)).toInt() - 1
        val maxX = ((viewBounds.x + viewBounds.width + layerTileWidth + layerTileWidth50) / layerTileWidth).toInt()

        val minY = (((viewBounds.y - layerTileHeight) / layerTileHeight50)).toInt() - 1
        val maxY = ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight50).toInt()

        val properties = layer.properties
        // this is kinda ass
        // TODO put this into custom layer or something so we dont have to do this each time
        val xOffsetRaw:String? = properties.get("xOffset", null, String::class.java)
        val xExtra = (xOffsetRaw?.toFloat()?: 0f) * unitScale

        val yOffsetRaw:String? = properties.get("yOffset", null, String::class.java)
        val yExtra = (yOffsetRaw?.toFloat()?: 0f) * unitScale

        for (ty in maxY - 1 downTo minY) {
            for (tx in maxX - 1 downTo minX) {
                // wrap around so we are always in map space
                val x = (tx % layerWidth + layerWidth) % layerWidth
                val y = (ty % layerHeight + layerHeight) % layerHeight
                val offsetX = if ((y % 2 == 1)) layerTileWidth50 else 0f
                // calculate offsets for tiles outside of map
                // todo this should always work, not just when view is to the left/bottom of the map
                val lxOffset = (if (tx < 0) -layerWidth * layerTileWidth else 0f)
                val lyOffset = (if (ty < 0) -layerHeight * layerTileHeight50 else 0f)
                val cell = layer.getCell(x, y) ?: continue
                val tile = cell.tile

                if (tile != null) {
                    val flipX = cell.flipHorizontally
                    val flipY = cell.flipVertically
                    val rotations = cell.rotation
                    val region = tile.textureRegion

                    val x1 = x * layerTileWidth - offsetX + tile.offsetX * unitScale + xExtra + lxOffset
                    val y1 = y * layerTileHeight50 + tile.offsetY * unitScale + yExtra + lyOffset
                    val x2 = x1 + region.regionWidth * unitScale
                    val y2 = y1 + region.regionHeight * unitScale

                    val u1 = region.u
                    val v1 = region.v2
                    val u2 = region.u2
                    val v2 = region.v

                    vertices[X1] = x1
                    vertices[Y1] = y1
                    vertices[C1] = color
                    vertices[U1] = u1
                    vertices[V1] = v1

                    vertices[X2] = x1
                    vertices[Y2] = y2
                    vertices[C2] = color
                    vertices[U2] = u1
                    vertices[V2] = v2

                    vertices[X3] = x2
                    vertices[Y3] = y2
                    vertices[C3] = color
                    vertices[U3] = u2
                    vertices[V3] = v2

                    vertices[X4] = x2
                    vertices[Y4] = y1
                    vertices[C4] = color
                    vertices[U4] = u2
                    vertices[V4] = v1

                    if (flipX) {
                        var temp = vertices[U1]
                        vertices[U1] = vertices[U3]
                        vertices[U3] = temp
                        temp = vertices[U2]
                        vertices[U2] = vertices[U4]
                        vertices[U4] = temp
                    }

                    if (flipY) {
                        var temp = vertices[V1]
                        vertices[V1] = vertices[V3]
                        vertices[V3] = temp
                        temp = vertices[V2]
                        vertices[V2] = vertices[V4]
                        vertices[V4] = temp
                    }

                    if (rotations != 0) {
                        when (rotations) {
                            TiledMapTileLayer.Cell.ROTATE_90 -> {
                                val tempV = vertices[V1]
                                vertices[V1] = vertices[V2]
                                vertices[V2] = vertices[V3]
                                vertices[V3] = vertices[V4]
                                vertices[V4] = tempV

                                val tempU = vertices[U1]
                                vertices[U1] = vertices[U2]
                                vertices[U2] = vertices[U3]
                                vertices[U3] = vertices[U4]
                                vertices[U4] = tempU
                            }
                            TiledMapTileLayer.Cell.ROTATE_180 -> {
                                var tempU = vertices[U1]
                                vertices[U1] = vertices[U3]
                                vertices[U3] = tempU
                                tempU = vertices[U2]
                                vertices[U2] = vertices[U4]
                                vertices[U4] = tempU
                                var tempV = vertices[V1]
                                vertices[V1] = vertices[V3]
                                vertices[V3] = tempV
                                tempV = vertices[V2]
                                vertices[V2] = vertices[V4]
                                vertices[V4] = tempV
                            }
                            TiledMapTileLayer.Cell.ROTATE_270 -> {
                                val tempV = vertices[V1]
                                vertices[V1] = vertices[V4]
                                vertices[V4] = vertices[V3]
                                vertices[V3] = vertices[V2]
                                vertices[V2] = tempV

                                val tempU = vertices[U1]
                                vertices[U1] = vertices[U4]
                                vertices[U4] = vertices[U3]
                                vertices[U3] = vertices[U2]
                                vertices[U2] = tempU
                            }
                        }
                    }
                    batch.draw(region.texture, vertices, 0, BatchTiledMapRenderer.NUM_VERTICES)
                }
            }
        }
    }
}
