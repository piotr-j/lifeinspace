package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import io.piotrjastrzebski.lis.INV_SCALE
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.utils.WrapTiledMapRenderer

/**
 * Created by PiotrJ on 22/12/15.
 */
// TODO this map size is too small, at least double that
var MAP_WIDTH = 64f
var MAP_HEIGHT = 48f
class MapRenderer() : BaseSystem(), SubRenderer {
    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @field:Wire lateinit var assets: Assets
    @field:Wire lateinit var batch: SpriteBatch
    @field:Wire lateinit var vb: ViewBounds

    lateinit var mapRenderer: WrapTiledMapRenderer

    override fun initialize() {
        mapRenderer = WrapTiledMapRenderer(assets.map, INV_SCALE, batch)
        var layer = assets.map.layers.get(0) as TiledMapTileLayer
        MAP_WIDTH = layer.width.toFloat()
        MAP_HEIGHT = layer.height.toFloat()

        // center camera on the map
        // TODO we probably want to center on a start spot in the map eventually
        camera.position.x = MAP_WIDTH/2;
        camera.position.y = MAP_HEIGHT/2;
        // render called externally
        isEnabled = false
    }

    override fun render() {
        mapRenderer.setOffsets(0f, 0f)
        mapRenderer.setView(camera.combined, vb.x, vb.y, vb.width, vb.height)
        mapRenderer.render()
        mapRenderer.setOffsets(-MAP_WIDTH, 0f)
        mapRenderer.setView(camera.combined, vb.x + MAP_WIDTH, vb.y, vb.width, vb.height)
        mapRenderer.render()
        mapRenderer.setOffsets(0f, -MAP_HEIGHT)
        mapRenderer.setView(camera.combined, vb.x, vb.y + MAP_HEIGHT, vb.width, vb.height)
        mapRenderer.render()
        mapRenderer.setOffsets(-MAP_WIDTH, -MAP_HEIGHT)
        mapRenderer.setView(camera.combined, vb.x + MAP_WIDTH, vb.y + MAP_HEIGHT, vb.width, vb.height)
        mapRenderer.render()
    }

    override fun processSystem() {}
}
