package io.piotrjastrzebski.lis.utils

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g3d.Model
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
    public lateinit var tilesModel: Model
    private val SKIN_PATH = "gui/uiskin.json"
    private val MODEL_TILES = "models/tiles.g3dj"
    private val MAP_PATH = "staggered.tmx"
    private val RADIAL_SHADER_PATH = "shaders/radial"
    private val assetManager: AssetManager

    init {
        assetManager = AssetManager()
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader())
        assetManager.setLoader(ShaderProgram::class.java, ShaderLoader())
        assetManager.load<Skin>(SKIN_PATH)
        assetManager.load<TiledMap>(MAP_PATH)
        assetManager.load<ShaderProgram>(RADIAL_SHADER_PATH)
        assetManager.load<Model>(MODEL_TILES)
    }

    // so we dont have to write
    // assetManager.load<Model>(MODEL_TILES, Model::class.java)
    // this is essentially a macro
    inline fun <reified T: Any> AssetManager.load(path: String) = this.load<T>(path, T::class.java)

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
        tilesModel = assetManager.get(MODEL_TILES, Model::class.java)
    }

    fun dispose() {
        assetManager.dispose()
        VisUI.dispose()
    }
}
