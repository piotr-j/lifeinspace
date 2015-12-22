package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import io.piotrjastrzebski.lis.VP_HEIGHT
import io.piotrjastrzebski.lis.VP_WIDTH
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class ViewBounds : BaseSystem() {
    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    public val vb = Rectangle()
    public var x = 0f
    public var y = 0f
    public var width = VP_WIDTH
    public var height = VP_HEIGHT
    override fun processSystem() {
        // TODO do we care about dirty or something?
        vb.set(camera.position.x - VP_WIDTH / 2f, camera.position.y - VP_HEIGHT / 2f, VP_WIDTH * camera.zoom, VP_HEIGHT * camera.zoom)
        x = vb.x
        y = vb.y
        width = vb.width
        height = vb.height
    }
}
