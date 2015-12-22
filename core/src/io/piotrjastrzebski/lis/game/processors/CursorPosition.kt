package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class CursorPosition : BaseSystem() {
    @field:Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    public var x = 0f
    public var y = 0f
    public val xy = Vector2()
    val tmp = Vector3()
    override fun processSystem() {
        camera.unproject(tmp.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f))
        xy.set(tmp.x, tmp.y)
        x = xy.x
        y = xy.y
    }
}
