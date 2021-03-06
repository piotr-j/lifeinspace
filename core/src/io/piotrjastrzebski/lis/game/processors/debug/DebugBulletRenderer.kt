package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import io.piotrjastrzebski.lis.game.processors.*
import io.piotrjastrzebski.lis.game.processors.physics.Physics

/**
 * Note: this is very slow
 * Created by PiotrJ on 25/12/15.
 */
class DebugBulletRenderer : BaseSystem(), SubRenderer {
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var modelRenderer: ModelRenderer
    @Wire lateinit var physics: Physics

    lateinit var debugDrawer: DebugDrawer
    var render = false

    override fun initialize() {
        keybinds.register(this, Input.Keys.F3, {toggle()}, {false})
        debugDrawer = DebugDrawer()
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
        physics.btWorld.debugDrawer = debugDrawer
        isEnabled = false
    }

    private fun toggle(): Boolean {
        render = !render
        Gdx.app.log("DebugBulletRenderer", "Render: $render")
        return true
    }

    override fun render() {
        if (!render) return
        modelRenderer.modelBatch.begin(modelRenderer.currentCamera)
        debugDrawer.begin(modelRenderer.modelBatch.camera)
        physics.btWorld.debugDrawWorld()
        debugDrawer.end()
        modelRenderer.modelBatch.end()
    }

    override fun processSystem() { }
}
