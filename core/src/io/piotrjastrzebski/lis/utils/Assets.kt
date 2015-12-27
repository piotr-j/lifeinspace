package io.piotrjastrzebski.lis.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.kotcrab.vis.ui.VisUI

/**
 * Created by EvilEntity on 21/12/2015.
 */
class Assets(val pixelScaleFactor: Float) {
    public lateinit var skin: Skin
    public lateinit var map: TiledMap
    public lateinit var radialShader: ShaderProgram
    private val SKIN_PATH = "gui/uiskin.json"
    private val MAP_PATH = "staggered.tmx"
    private val RADIAL_SHADER_PATH = "shaders/radial"
    private val assetManager: AssetManager

    init {
        assetManager = AssetManager()
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader())
        assetManager.setLoader(ShaderProgram::class.java, ShaderLoader())
        assetManager.load<Skin>(SKIN_PATH, Skin::class.java)
        assetManager.load<TiledMap>(MAP_PATH, TiledMap::class.java)
        assetManager.load<ShaderProgram>(RADIAL_SHADER_PATH, ShaderProgram::class.java)
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
        radialShader = assetManager.get(RADIAL_SHADER_PATH, ShaderProgram::class.java)
    }

    fun dispose() {
        assetManager.dispose()
        VisUI.dispose()
    }
}
