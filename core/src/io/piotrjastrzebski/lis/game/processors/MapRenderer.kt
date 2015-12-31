package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import io.piotrjastrzebski.lis.INV_SCALE
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.utils.WrapIsoStaggeredRenderer

/**
 * Created by PiotrJ on 22/12/15.
 */
var MAP_WIDTH = 64f
var MAP_HEIGHT = 48f
class MapRenderer() : BaseSystem(), SubRenderer {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var assets: Assets
    @Wire lateinit var batch: SpriteBatch
    @Wire lateinit var vb: ViewBounds

    lateinit var mapRenderer: WrapIsoStaggeredRenderer

    override fun initialize() {
        mapRenderer = WrapIsoStaggeredRenderer(assets.map, INV_SCALE, batch)
        var layer = assets.map.layers.get(0) as TiledMapTileLayer
        // we are iso now, so tiles are no longer 1x1
        MAP_WIDTH = layer.width * layer.tileWidth * INV_SCALE
        // .5 cus the tiles overlap in width
        MAP_HEIGHT = layer.height * layer.tileHeight * INV_SCALE * .5f

        // render called externally
        isEnabled = false
    }

    override fun render() {
        mapRenderer.setView(camera.combined, vb.x, vb.y, vb.width, vb.height)
        mapRenderer.render()
//        mapRenderer.render(intArrayOf(1))
    }

    override fun processSystem() {}
}
