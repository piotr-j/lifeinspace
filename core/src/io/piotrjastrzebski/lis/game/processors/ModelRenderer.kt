package io.piotrjastrzebski.lis.game.processors

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import io.piotrjastrzebski.lis.game.components.Culled
import io.piotrjastrzebski.lis.game.components.RenderableModel
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by PiotrJ on 22/12/15.
 */
class ModelRenderer() : IteratingSystem(Aspect.all(RenderableModel::class.java).exclude(Culled::class.java)), SubRenderer {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var mRenderableModel: ComponentMapper<RenderableModel>

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
        // TODO this looks kinda fine, but we want perspective cam for debug purposes
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
    }

    override fun render() {
        camController.update()
        modelBatch.begin(camera)
        modelBatch.render<ModelInstance>(instances, environment)
        modelBatch.end()
    }

    override fun begin() {
        // clear instances, add all the crap
        instances.size = 0
    }

    override fun process(entityId: Int) {
        instances.add(mRenderableModel.get(entityId).instance)
    }
}
