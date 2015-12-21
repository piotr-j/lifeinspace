package io.piotrjastrzebski.lis.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.kotcrab.vis.ui.VisUI

/**
 * Created by EvilEntity on 21/12/2015.
 */
class Assets(val pixelScaleFactor: Float) {
    protected lateinit var skin: Skin get
    private val SKIN_PATH = "gui/uiskin.json"
    private val assetManager: AssetManager

    init {
        assetManager = AssetManager()
        assetManager.load<Skin>(SKIN_PATH, Skin::class.java)
        // TODO load assets and stuff
    }

    private var loaded = false

    fun update(): Boolean {
        val isDone = assetManager.update()
        if (isDone && !loaded) {
            finishLoading()
            loaded = true
        }
        return isDone
    }

    private fun finishLoading() {
        VisUI.load(if (pixelScaleFactor > 1.5f) VisUI.SkinScale.X2 else VisUI.SkinScale.X1)

        skin = assetManager.get(SKIN_PATH, Skin::class.java)
    }

    fun dispose() {
        assetManager.dispose()
    }
}
