package io.piotrjastrzebski.lis.utils

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Array

/**
 * Created by PiotrJ on 22/12/15.
 */
class ShaderLoader(resolver: FileHandleResolver = InternalFileHandleResolver()) : AsynchronousAssetLoader<ShaderProgram, ShaderLoader.ShaderParameter>(resolver) {

    private val data = object {
        var vertex = ""
        var fragment = ""
    }

    override fun loadAsync(manager: AssetManager, fileName: String, file: FileHandle, parameter: ShaderParameter?) {
        data.vertex = resolve(fileName + ".vert").readString()
        data.fragment = resolve(fileName + ".frag").readString()
    }

    override fun loadSync(manager: AssetManager, fileName: String, file: FileHandle,
                          parameter: ShaderParameter?): ShaderProgram {

        val wasPedantic = ShaderProgram.pedantic
        if (parameter != null) ShaderProgram.pedantic = parameter.pedantic
        val program = ShaderProgram(data.vertex, data.fragment)
        ShaderProgram.pedantic = wasPedantic

        val shader = ShaderProgram(data.vertex, data.fragment)
        if (!program.isCompiled) {
            Gdx.app.error("Failed to load shader $fileName:\n", program.log)
        }
        return shader
    }

    override fun getDependencies(fileName: String, file: FileHandle, parameter: ShaderParameter?): Array<AssetDescriptor<Any>>? {
        return null
    }

    inner class ShaderParameter : AssetLoaderParameters<ShaderProgram>() {
        // ShaderProgram.pedantic value
        var pedantic = true
    }
}
