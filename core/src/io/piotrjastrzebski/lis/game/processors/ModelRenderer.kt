package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.IntMap
import io.piotrjastrzebski.lis.INV_SCALE
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.utils.WrapIsoStaggeredRenderer

/**
 * Created by PiotrJ on 22/12/15.
 */
class ModelRenderer() : BaseSystem(), SubRenderer {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var assets: Assets

    val modelBatch = ModelBatch()
    val environment = Environment()
    var instances = com.badlogic.gdx.utils.Array<ModelInstance>()

    var cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
//    var camController: CameraInputController? = null
    var camController = CameraInputController(cam)

    override fun initialize() {
//        cam.rotate(Vector3.X, -45f)
        cam.position.set(0f, -10f, 10f)
//        cam.rotate(Vector3.X, -45f)
//        cam.rotate(cam.up, 90f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()

//        camera.position.x = -10f
        camera.near = 1f
        camera.far = 300f
        camera.position.x = 0f
        camera.position.y = -15f
        camera.position.z = 10f
        camera.lookAt(0f, 0f, 0f)
//        camera.position.x = 0f
//        camera.position.y = 0f
//        camera.position.z = 0f
//        camera.lookAt(10f, -10f, 10f)
        camera.update()

        camController = CameraInputController(camera)
//        camController = CameraInputController(cam)
        val multi = Gdx.input.inputProcessor as InputMultiplexer
        multi.addProcessor(0, camController)
//        Gdx.input.inputProcessor = camController

        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))
        environment.add(DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, -0.8f, -0.2f))
        val layer1 = assets.map.layers.get(1) as TiledMapTileLayer
        val tiles = assets.tilesModel

        val tileIdToNameRot = IntMap<Pair<String, Float>>()
        val tileSet = assets.map.tileSets.getTileSet(0)
        fun dirToRot(dir:String):Float {
            when (dir) {
                "se" -> return 0f
                "ne" -> return 90f
                "nw" -> return 180f
                "sw" -> return 270f
                else -> return 0f
            }
        }
        for (id in 0..tileSet.size() -1) {
            val props = tileSet.getTile(id)?.properties ?: continue
            if (props.containsKey("id")) {
                val tileId = props.get("id", String::class.java)
                // TODO need to make sure which one is default rot
                val tileDir = props.get("dir", "sw", String::class.java)
                tileIdToNameRot.put(id, tileId to dirToRot(tileDir))
                Gdx.app.log("", "${tileIdToNameRot.get(id)}")
            }
        }

        val layers = assets.map.layers
        val quat = Quaternion()
        var zOffset = 1f
        val unit = Math.sqrt(2.0).toFloat()
        for (layer in layers) {
            layer as TiledMapTileLayer
            for (x in 0..layer.width) {
                for (y in 0..layer.height) {
                    val offsetX = if ((y % 2 == 1)) unit/2 else 0f
                    val cell: TiledMapTileLayer.Cell? = layer.getCell(x, y) ?: continue
                    val tile: TiledMapTile? = cell!!.tile ?: continue
                    // TODO get id from tile
                    if (!tileIdToNameRot.containsKey(tile!!.id)) continue
                    val pair = tileIdToNameRot.get(tile.id)
                    val id = pair.first
                    //                val id = "tile"
                    val instance:ModelInstance = ModelInstance(tiles, id)
                    if (instance.nodes.size == 0) continue
                    val node = instance.getNode(id)

                    instance.transform.set(node.globalTransform)
                    node.translation.set(0f, 0f, 0f)
                    node.scale.set(1f, 1f, 1f)
                    node.rotation.set(Vector3.Z, -90f)
                    node.rotation.mul(quat.set(Vector3.Z, pair.second + 45))
                    //                node.rotation.mul(quat.set(Vector3.X, -45f))
                    instance.calculateTransforms()

                    instance.transform.setToTranslation(x*unit - offsetX, y*unit/2, zOffset)
                    instances.add(instance)
                }
            }
            zOffset+=.25f
        }
        isEnabled = false
    }

    override fun render() {
        camController.update()
        modelBatch.begin(camera)
//        modelBatch.begin(cam)
        modelBatch.render<ModelInstance>(instances, environment)
        modelBatch.end()
    }

    override fun processSystem() {}
}
