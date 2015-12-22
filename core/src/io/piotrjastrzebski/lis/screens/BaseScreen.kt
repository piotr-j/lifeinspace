package io.piotrjastrzebski.lis.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import io.piotrjastrzebski.lis.LiSGame
import io.piotrjastrzebski.lis.VP_HEIGHT
import io.piotrjastrzebski.lis.VP_WIDTH
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by EvilEntity on 21/12/2015.
 */
abstract class BaseScreen(val game: LiSGame) : Screen, InputProcessor {
    protected val gameCamera = OrthographicCamera()
    protected val gameViewport = ExtendViewport(VP_WIDTH, VP_HEIGHT, gameCamera)
    protected val guiCamera = OrthographicCamera()
    protected val guiViewport = ScreenViewport(guiCamera)

    protected val batch: SpriteBatch by lazy { game.batch }
    protected val shapeRenderer: ShapeRenderer by lazy { game.renderer }
    protected val assets: Assets by lazy { game.assets }

    protected val stage: Stage
    protected var root: Table

    protected val multiplexer: InputMultiplexer

    init {
        stage = Stage(guiViewport, batch)
        root = Table()
        root.setFillParent(true)
        stage.addActor(root)
        multiplexer = InputMultiplexer(stage, this)
        Gdx.input.inputProcessor = multiplexer
    }

    override fun resize(width: Int, height: Int) {
        gameViewport.update(width, height, false)
        guiViewport.update(width, height, true)
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    }

    override fun resume() {}

    override fun show() {}

    override fun hide() {
        dispose()
    }
    override fun pause() {}
    override fun dispose() {}

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
    override fun mouseMoved(screenX: Int, screenY: Int) = false
    override fun keyTyped(character: Char) = false
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false
    override fun scrolled(amount: Int) = false
    override fun keyUp(keycode: Int) = false
    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false
    override fun keyDown(keycode: Int) = false
}
