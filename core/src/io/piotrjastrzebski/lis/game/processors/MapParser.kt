package io.piotrjastrzebski.lis.game.processors

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import io.piotrjastrzebski.lis.game.components.BoxDef
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.BodyDef
import io.piotrjastrzebski.lis.utils.Assets

/**
 * Created by PiotrJ on 22/12/15.
 */
class MapParser() : BaseSystem() {
    @Wire lateinit var assets: Assets

    override fun initialize() {
        // eh, on insert map?
        val walls = assets.map.layers.get("walls") as TiledMapTileLayer
        for (x in 0..walls.width) {
            for (y in 0..walls.height) {
                val cell: TiledMapTileLayer.Cell? = walls.getCell(x, y) ?: continue
                val tile: TiledMapTile? = cell!!.tile ?: continue
                createTile(tile!!, x, y)
            }
        }

        isEnabled = false
    }

    private fun createTile(tile: TiledMapTile, x: Int, y: Int) {
        Gdx.app.log("MapParser", "Tiled ${tile.id} at $x, $y")
        // TODO eventually, we want to render only base layer and replace other tiles with entities
        val e = world.createEntity().edit()
        val trans = e.create(Transform::class.java)
        trans.xy.set(x.toFloat(), y.toFloat())
        trans.bounds.setSize(1f, 1f)
        val def = e.create(BodyDef::class.java)
        def.def.type = BodyType.StaticBody
        val box = e.create(BoxDef::class.java)
        box.size(.5f, .5f)
    }

    override fun processSystem() {}
}
