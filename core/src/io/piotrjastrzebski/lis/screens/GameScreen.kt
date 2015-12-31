package io.piotrjastrzebski.lis.screens

import com.artemis.InvocationStrategy
import com.artemis.World
import com.artemis.WorldConfiguration
import io.piotrjastrzebski.lis.LiSGame
import io.piotrjastrzebski.lis.game.processors.*
import io.piotrjastrzebski.lis.game.processors.CameraFollow
import io.piotrjastrzebski.lis.game.processors.debug.DebugBox2dRenderer
import io.piotrjastrzebski.lis.game.processors.debug.DebugCameraController
import io.piotrjastrzebski.lis.game.processors.debug.DebugTileGridRenderer
import io.piotrjastrzebski.lis.game.processors.debug.DebugTileSelectRenderer
import io.piotrjastrzebski.lis.game.processors.physics.BodyInit
import io.piotrjastrzebski.lis.game.processors.physics.BodyPositionWrap
import io.piotrjastrzebski.lis.game.processors.physics.Physics
import io.piotrjastrzebski.lis.utils.Resizing

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

//        config.setSystem(MapParser())
        config.setSystem(PlayerSpawner())

        config.setSystem(CursorPosition())
        config.setSystem(Physics())
        config.setSystem(BodyInit())
        config.setSystem(BodyPositionWrap())
        config.setSystem(PlayerController())
        config.setSystem(DebugCameraController())
        config.setSystem(CameraFollow())
        // NOTE stuff that changes camera must be before CameraUpdate
        config.setSystem(CameraUpdate())
        config.setSystem(ViewBounds())

        config.setSystem(Renderer())
        config.setSystem(MapRenderer())
        config.setSystem(ModelRenderer())
        config.setSystem(DebugTileGridRenderer())
        config.setSystem(DebugTileSelectRenderer())
        config.setSystem(DebugBox2dRenderer())
        val kbs = KeyBindings()
        multiplexer.addProcessor(kbs)
        config.setSystem(kbs)
        config.setInvocationStrategy(InvocationStrategy())

        world = World(config)
    }

    override fun render(delta: Float) {
        super.render(delta)
        world.delta = delta
        world.process()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        // TODO filter, forEach?
        for (system in world.systems) {
            if (system is Resizing) system.resize(width, height)
        }
    }

    override fun dispose() {
        world.dispose()
        super.dispose()
    }
}
