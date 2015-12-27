package io.piotrjastrzebski.lis

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.piotrjastrzebski.lis.screens.LoadingScreen
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by EvilEntity on 20/12/2015.
 */
public const val SCALE = 48f;
public const val INV_SCALE = 1f/SCALE;
public const val VP_WIDTH = 1280 * INV_SCALE;
public const val VP_HEIGHT = 720 * INV_SCALE;

class LiSGame(val bridge: PlatformBridge) : Game() {
    internal val assets: Assets by lazy { Assets(bridge.getPixelScaleFactor()) }
    internal val batch: SpriteBatch by lazy { SpriteBatch()}
    internal val renderer: ShapeRenderer by lazy { ShapeRenderer()}

    override fun create() {
        setScreen(LoadingScreen(this))
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assets.dispose()
    }
}
