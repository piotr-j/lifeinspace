package io.piotrjastrzebski.lis.screens

import io.piotrjastrzebski.lis.LiSGame

/**
 * Created by EvilEntity on 21/12/2015.
 */

class MainMenuScreen(game: LiSGame) : BaseScreen(game) {
    init {
        // TODO gui to start game and other things
    }
    override fun render(delta: Float) {
        super.render(delta)
        game.screen = GameScreen(game)
    }
}
