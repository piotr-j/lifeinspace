package io.piotrjastrzebski.lis.screens

import io.piotrjastrzebski.lis.LiSGame

/**
 * Created by EvilEntity on 21/12/2015.
 */

class LoadingScreen(game: LiSGame) : BaseScreen(game) {
    init {
        // TODO load some temp asset as loading screen
    }

    override fun render(delta: Float) {
        super.render(delta)
        if (assets.update()) game.screen = MainMenuScreen(game)
        batch.projectionMatrix = gameCamera.combined
        batch.begin()
        // TODO draw loading img stuff
        batch.end()
    }

    override fun dispose() {
        super.dispose()
        // TODO nuke img stuff
    }
}
