package io.piotrjastrzebski.lis.game.processors.physics

import com.artemis.Aspect
import com.artemis.ComponentMapper
import com.artemis.Entity
import com.artemis.EntitySystem
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import io.piotrjastrzebski.lis.game.components.Dynamic
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.Body
import io.piotrjastrzebski.lis.game.components.physics.BodyDef
import io.piotrjastrzebski.lis.game.processors.MAP_HEIGHT
import io.piotrjastrzebski.lis.game.processors.MAP_WIDTH

/**
 * Created by PiotrJ on 25/12/15.
 */
class BodyPositionWrap : IteratingSystem(Aspect.all(Body::class.java, Transform::class.java, Dynamic::class.java)) {

    @Wire lateinit var mTransform: ComponentMapper<Transform>
    @Wire lateinit var mBody: ComponentMapper<Body>

    override fun initialize() {

    }

    val tmp = Vector2()
    val offset = Vector2()
    override fun process(entityId: Int) {
        val trans = mTransform.get(entityId)
        val body = mBody.get(entityId).body!!
        tmp.set(body.position).sub(trans.bounds.width/2f, trans.bounds.height/2f)

        offset.setZero()
        if (tmp.x < 0) {
            offset.x = MAP_WIDTH
        } else if (tmp.x > MAP_WIDTH){
            offset.x = -MAP_WIDTH
        }
        if (tmp.y < 0) {
            offset.y = MAP_HEIGHT
        } else if (tmp.y > MAP_HEIGHT) {
            offset.y = -MAP_HEIGHT
        }
        if (!offset.isZero) {
            tmp.add(offset).add(trans.bounds.width/2f, trans.bounds.height/2f)
            body.setTransform(tmp, body.angle)
            Gdx.app.log("", "teleport!")
        }
    }
}
