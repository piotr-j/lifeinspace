package io.piotrjastrzebski.lis.screens

import com.artemis.World
import com.artemis.WorldConfiguration
import com.badlogic.gdx.Gdx
import io.piotrjastrzebski.lis.LiSGame

/**
 * Created by EvilEntity on 21/12/2015.
 */
public const val WIRE_GAME_CAM = "game-cam"
public const val WIRE_GAME_VP = "game-vp"
public const val WIRE_GUI_CAM = "gui-cam"
public const val WIRE_GUI_VP = "gui-vp"

class GameScreen(game: LiSGame) : BaseScreen(game) {
    val world:World

    init {
        var config = WorldConfiguration()
        config.register(WIRE_GAME_CAM, gameCamera)
        config.register(WIRE_GAME_VP, gameViewport)
        config.register(WIRE_GUI_CAM, guiCamera)
        config.register(WIRE_GUI_VP, guiViewport)
        config.register(shapeRenderer)
        config.register(batch)
        config.register(assets)
        // TODO systems and all this stuff

        world = World(config)
    }

    override fun render(delta: Float) {
        super.render(delta)
        Gdx.app.log("TODO", "Game!")
        world.delta = delta
        world.process()
    }

    override fun dispose() {
        super.dispose()
        world.dispose()
    }
}
