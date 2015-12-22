package io.piotrjastrzebski.lis.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.kotcrab.vis.ui.VisUI

/**
 * Created by EvilEntity on 21/12/2015.
 */
class Assets(val pixelScaleFactor: Float) {
    protected lateinit var skin: Skin get
    protected lateinit var map :TiledMap
    private val SKIN_PATH = "gui/uiskin.json"
    private val MAP_PATH = "map.tmx"
    private val assetManager: AssetManager

    init {
        assetManager = AssetManager()
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader())
        assetManager.load<Skin>(SKIN_PATH, Skin::class.java)
        assetManager.load<TiledMap>(MAP_PATH, TiledMap::class.java)
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
        map = assetManager.get(MAP_PATH, TiledMap::class.java)
    }

    fun dispose() {
        assetManager.dispose()
    }
}
