package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.viewport.ExtendViewport
import io.piotrjastrzebski.lis.VP_HEIGHT
import io.piotrjastrzebski.lis.VP_WIDTH
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.screens.WIRE_GAME_VP

/**
 * Created by EvilEntity on 22/12/2015.
 */
class ViewBounds : BaseSystem() {
    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @field:Wire(name = WIRE_GAME_VP) lateinit var viewport: ExtendViewport
    public val vb = Rectangle()
    public var x = 0f
    public var y = 0f
    public var width = VP_WIDTH
    public var height = VP_HEIGHT
    override fun processSystem() {
        // TODO do we care about dirty or something?
        vb.set(camera.position.x - viewport.worldWidth / 2f, camera.position.y - viewport.worldHeight / 2f,
                viewport.worldWidth * camera.zoom, viewport.worldHeight * camera.zoom)
        x = vb.x
        y = vb.y
        width = vb.width
        height = vb.height
    }
}
