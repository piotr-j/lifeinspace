package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import io.piotrjastrzebski.lis.game.processors.debug.DebugBox2dRenderer
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.utils.Resizing

/**
 * Created by PiotrJ on 22/12/15.
 */
class Renderer() : BaseSystem(), Resizing {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var assets: Assets
    @Wire lateinit var batch: SpriteBatch
    @Wire lateinit var vb: ViewBounds
    @Wire lateinit var mapRenderer: MapRenderer
    @Wire lateinit var box2dRenderer: DebugBox2dRenderer
    @Wire lateinit var keybinds: KeyBindings
    var fbo: FrameBuffer? = null
    val fboRegion = TextureRegion()
    var shader: ShaderProgram? = null

    override fun initialize() {
        keybinds.register(Input.Keys.F12, {toggleRadialShader()}, {false})
        shader = assets.radialShader
        Gdx.app.log("Renderer", "F12 - toggle radial shader")
    }

    private fun toggleRadialShader(): Boolean {
        if (shader != null)
            shader = null
        else
            shader = assets.radialShader
        return true
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
        box2dRenderer.render()
        fbo!!.end()

        batch.shader = shader
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
