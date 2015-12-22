package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class CameraMove : BaseSystem() {
    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    override fun processSystem() {
        var scale = if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) 25f else 5f
        scale *= world.delta
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.position.x -= scale
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.position.x += scale
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.position.y += scale
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.position.y -= scale
        }
    }
}
