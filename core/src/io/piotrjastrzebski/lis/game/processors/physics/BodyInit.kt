package io.piotrjastrzebski.lis.game.processors.physics

import com.artemis.*
import com.artemis.annotations.Wire
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import io.piotrjastrzebski.lis.game.components.BoxDef
import io.piotrjastrzebski.lis.game.components.physics.Body
import io.piotrjastrzebski.lis.game.components.physics.BodyDef
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.components.physics.CircleDef
import io.piotrjastrzebski.lis.game.components.physics.PolygonDef

/**
 * Created by PiotrJ on 25/12/15.
 */
class BodyInit : BaseEntitySystem(Aspect.all(BodyDef::class.java, Transform::class.java).one(BoxDef::class.java, CircleDef::class.java, PolygonDef::class.java)) {
    @field:Wire lateinit var physics: Physics
    @field:Wire lateinit var mTransform: ComponentMapper<Transform>
    @field:Wire lateinit var mBodyDef: ComponentMapper<BodyDef>
    @field:Wire lateinit var mBody: ComponentMapper<Body>
    @field:Wire lateinit var mBoxDef: ComponentMapper<BoxDef>
    @field:Wire lateinit var mCircleDef: ComponentMapper<CircleDef>
    @field:Wire lateinit var mPolygonDef: ComponentMapper<PolygonDef>

    val fixtureDef = FixtureDef()
    var polygon = PolygonShape()
    var circle = CircleShape()

    override fun initialize() {
        isEnabled = false
    }

    override fun inserted(e: Int) {
        val body = mBody.create(e)
        val trans = mTransform.get(e)
        val def = mBodyDef.get(e)
        def.def.position.set(trans.xy)
        def.def.angle = trans.angle * MathUtils.degreesToRadians
        // we will assume for now that other thins are setup already
        val b2dBody = physics.box2d.createBody(def.def)
        body.body = b2dBody

        fixtureDef.restitution = def.restitution
        fixtureDef.friction = def.friction
        fixtureDef.density = def.density
        fixtureDef.filter.categoryBits = def.categoryBits
        fixtureDef.filter.maskBits = def.maskBits
        fixtureDef.filter.groupIndex = def.groupIndex

        val boxDef = mBoxDef.getSafe(e)
        if (boxDef != null) {
            polygon.setAsBox(boxDef.width, boxDef.height)
            fixtureDef.shape = polygon
            b2dBody.createFixture(fixtureDef)

            b2dBody.setTransform(trans.xy.x + boxDef.width, trans.xy.y + boxDef.height, trans.angle)
        }

//        val polygonDef = mPolygonDef.getSafe(e)
//        if (polygonDef != null) {
//            polygon.set(polygonDef!!.get())
//            fixtureDef.shape = polygon
//            body.body!!.createFixture(fixtureDef)
//            val bounds = polygonDef!!.getBounds()
//            body.setTransform(transform.pos.x + bounds.width / 2, transform.pos.y + +bounds.height / 2, transform.rot)
//        }

        val circleDef = mCircleDef.getSafe(e)
        if (circleDef != null) {
            circle.radius = circleDef.radius
            fixtureDef.shape = circle
            b2dBody.createFixture(fixtureDef)
            b2dBody.setTransform(trans.xy.x + circleDef.radius, trans.xy.y + circleDef.radius, trans.angle)
        }
    }

    override fun removed(e: Int) {
        val body = mBody.get(e)
        physics.box2d.destroyBody(body.body)
    }

    override fun processSystem() { }
}
