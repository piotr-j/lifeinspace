package io.piotrjastrzebski.lis.game.processors

import com.artemis.Aspect
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Rectangle
import io.piotrjastrzebski.lis.INV_SCALE
import io.piotrjastrzebski.lis.VP_HEIGHT
import io.piotrjastrzebski.lis.VP_WIDTH
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.utils.Resizing
import io.piotrjastrzebski.lis.utils.WrapTiledMapRenderer

/**
 * Created by PiotrJ on 22/12/15.
 */
class MapRenderer() : IteratingSystem(Aspect.all(Transform::class.java)), Resizing {
    var radialFbo: FrameBuffer? = null
    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @field:Wire lateinit var assets: Assets
    @field:Wire lateinit var batch: SpriteBatch
    var mapWidth = 64f
    var mapHeight = 48f
    val vb = Rectangle()

    lateinit var mapRenderer: WrapTiledMapRenderer

    init {
        // TODO do we want this to be iterating?

    }

    override fun initialize() {
        mapRenderer = WrapTiledMapRenderer(assets.map, INV_SCALE, batch)
        var layer = assets.map.layers.get(0) as TiledMapTileLayer
        mapWidth = layer.width.toFloat()
        mapHeight = layer.height.toFloat()

    }

    override fun begin() {
        vb.set(-VP_WIDTH/2, -VP_HEIGHT/2, VP_WIDTH, VP_HEIGHT)
        batch.projectionMatrix = camera.combined
        batch.begin()
        mapRenderer.setOffsets(0f, 0f)
        mapRenderer.setView(camera.combined, vb.x, vb.y, vb.width, vb.height)
        mapRenderer.render()
        mapRenderer.setOffsets(-mapHeight, 0f)
        mapRenderer.setView(camera.combined, vb.x + mapWidth, vb.y, vb.width, vb.height)
        mapRenderer.render()
        mapRenderer.setOffsets(0f, -mapHeight)
        mapRenderer.setView(camera.combined, vb.x, vb.y + mapHeight, vb.width, vb.height)
        mapRenderer.render()
        mapRenderer.setOffsets(-mapWidth, -mapWidth)
        mapRenderer.setView(camera.combined, vb.x + mapWidth, vb.y + mapHeight, vb.width, vb.height)
        mapRenderer.render()
        batch.end()
//        mapRenderer.setView(camera.combined, x - VP_WIDTH/2, y, VP_WIDTH, VP_HEIGHT)
//        mapRenderer.render()
//        mapRenderer.setView(camera.combined, x - VP_WIDTH/2, y - VP_HEIGHT/2, VP_WIDTH, VP_HEIGHT)
//        mapRenderer.render()
//        mapRenderer.setView(camera.combined, x, y - VP_HEIGHT/2, VP_WIDTH, VP_HEIGHT)
//        mapRenderer.render()
    }

    override fun process(entityId: Int) {

    }

    override fun end() {

    }

    override fun resize(width: Int, height: Int) {
        radialFbo?.dispose()
        radialFbo = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
    }

    override fun dispose() {
        radialFbo?.dispose()
    }
}
