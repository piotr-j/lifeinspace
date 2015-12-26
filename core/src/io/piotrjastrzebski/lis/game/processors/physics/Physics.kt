package io.piotrjastrzebski.lis.game.processors.physics

import com.artemis.BaseSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.ObjectSet

/**
 *
 * Systems that are on variable schedule should be after this one
 * so alpha values is correct
 * Created by PiotrJ on 25/12/15.
 */
class Physics : BaseSystem() {
    public val STEP_TIME = 1.0f / 30.0f
    public val MAX_STEPS = 3
    public val VELOCITY_ITERS = 6
    public val POSITION_ITERS = 2
    var accumulator = 0f
    public var alpha = 0f
    val box2d = World(Vector2(), true)
    val fixedUpdatables = ObjectSet<FixedUpdatable>()
    override fun processSystem() {
        accumulator += world.delta
        var steps = 0
        while (STEP_TIME < accumulator && MAX_STEPS > steps) {
            // TODO do we need this?
            //	box2dWorld.clearForces();
            // fixed update before step works a lot better
//            println("Fixed update")
            for (fixed in fixedUpdatables) {
                fixed.fixedUpdate()
            }
            box2d.step(STEP_TIME, VELOCITY_ITERS, POSITION_ITERS)
            accumulator -= STEP_TIME
            steps++
        }
        alpha = accumulator / STEP_TIME
//        println("Variable update $alpha")
    }

    override fun dispose() {
        box2d.dispose()
    }

    public fun register(updatable: FixedUpdatable) {
        fixedUpdatables.add(updatable)
    }

    public fun unregister(updatable: FixedUpdatable) {
        fixedUpdatables.remove(updatable)
    }
}

interface FixedUpdatable {
    fun fixedUpdate()
}
