package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import io.piotrjastrzebski.lis.INV_SCALE
import io.piotrjastrzebski.lis.VP_HEIGHT
import io.piotrjastrzebski.lis.VP_WIDTH
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.utils.Resizing
import io.piotrjastrzebski.lis.utils.WrapTiledMapRenderer

/**
 * Created by PiotrJ on 22/12/15.
 */
class Renderer() : BaseSystem(), Resizing {
    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @field:Wire lateinit var assets: Assets
    @field:Wire lateinit var batch: SpriteBatch
    @field:Wire lateinit var vb: ViewBounds
    @field:Wire lateinit var mapRenderer: MapRenderer
    var fbo: FrameBuffer? = null
    val fboRegion = TextureRegion()

    override fun initialize() {

    }

    override fun processSystem() {
        fbo!!.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.projectionMatrix = camera.combined
        batch.begin()
        mapRenderer.render()
        // TODO render entities or whatever
        batch.end()
        fbo!!.end()

        // TODO we want an option to disable this
        batch.shader = assets.radialShader
        batch.begin()
        batch.draw(fboRegion, camera.position.x - vb.width/2, camera.position.y - vb.height/2, vb.width, vb.height)
        batch.end()
        batch.shader = null
    }

    override fun resize(width: Int, height: Int) {
        fbo?.dispose()
        fbo = FrameBuffer(Pixmap.Format.RGBA8888, width, height, false)
        fboRegion.setRegion(fbo!!.colorBufferTexture)
        fboRegion.flip(false, true)

        val shader = assets.radialShader
        shader.begin()
        shader.setUniformf("distortion", .4f)
        shader.setUniformf("zoom", 3.7f)
        // TODO use it?
//        shader.setUniformf("resolution", width.toFloat(), height.toFloat())
        shader.end()
    }
}

interface SubRenderer {
    fun render()
}
