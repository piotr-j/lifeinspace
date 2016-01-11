package io.piotrjastrzebski.lis.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ObjectMap
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
    private val nameToModel = ObjectMap<String, Model>()

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

        // TODO do this properly
        nameToModel.put("tiles", tilesModel)

        val textures = com.badlogic.gdx.utils.Array<Texture>()
        assetManager.getAll(Texture::class.java, textures)
        for (texture in textures) {
            texture.bind()
            // so stuff doenst look like ass when angled
            // TODO probably need to get the max value from somewhere
            Gdx.gl.glTexParameterf(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAX_ANISOTROPY_EXT, 16f)
        }

    };

    public fun getModelInstance(path:String, nodeId: String): ModelInstance? {
        val model = nameToModel.get(path, null)?: return null
        return ModelInstance(model, nodeId)
    }

    fun dispose() {
        assetManager.dispose()
        VisUI.dispose()
    }
}
