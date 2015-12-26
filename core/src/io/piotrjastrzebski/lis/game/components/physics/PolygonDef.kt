package io.piotrjastrzebski.lis.game.components.physics

import com.artemis.PooledComponent
import com.artemis.annotations.Transient
import com.badlogic.gdx.math.Polygon

/**
 * Created by PiotrJ on 22/12/15.
 */
// TODO check if annotation works
@Transient
class PolygonDef() : PooledComponent() {
    var polygon = Polygon()

    fun set(verts: FloatArray): PolygonDef {
        polygon.vertices = verts
        return this
    }

    fun get() = polygon.vertices


    fun getBounds() =polygon.boundingRectangle

    override fun reset() { }
}
