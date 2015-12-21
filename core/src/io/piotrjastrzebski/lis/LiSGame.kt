package io.piotrjastrzebski.lis

import com.badlogic.gdx.Game
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import io.piotrjastrzebski.lis.screens.LoadingScreen
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by EvilEntity on 20/12/2015.
 */
public const val SCALE = 32f;
public const val INV_SCALE = 1f/SCALE;
public const val VP_WIDTH = 1280 * INV_SCALE;
public const val VP_HEIGHT = 720 * INV_SCALE;

class LiSGame(val bridge: PlatformBridge) : Game() {
    internal lateinit var assets: Assets
    internal lateinit var batch: SpriteBatch
    internal lateinit var renderer: ShapeRenderer

    override fun create() {
        assets = Assets(bridge.getPixelScaleFactor())
        batch = SpriteBatch()
        renderer = ShapeRenderer()
        setScreen(LoadingScreen(this))
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
        assets.dispose()
    }
}
