package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class CameraUpdate : BaseSystem() {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var vb: ViewBounds

    override fun processSystem() {
        // if we move outside of desire range, we correct
        // this way we are always near origin andwe dont have to move the maps
        if (camera.position.x < -MAP_WIDTH / 2) camera.position.x += MAP_WIDTH
        if (camera.position.x > MAP_WIDTH / 2) camera.position.x -= MAP_WIDTH
        if (camera.position.y < -MAP_HEIGHT / 4) camera.position.y += MAP_HEIGHT
        if (camera.position.y > MAP_HEIGHT / 4) camera.position.y -= MAP_HEIGHT
        camera.update()
    }
}
