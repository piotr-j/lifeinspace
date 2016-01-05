package io.piotrjastrzebski.lis.game.processors.debug

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.physics.bullet.DebugDrawer
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw
import io.piotrjastrzebski.lis.game.processors.*

/**
 * Created by PiotrJ on 25/12/15.
 */
class DebugBulletRenderer : BaseSystem(), SubRenderer {
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var modelRenderer: ModelRenderer

    lateinit var debugDrawer: DebugDrawer
    var render = false

    override fun initialize() {
        keybinds.register(Input.Keys.F3, {toggle()}, {false})
        debugDrawer = DebugDrawer()
        debugDrawer.debugMode = btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
        modelRenderer.dynamicsWorld.debugDrawer = debugDrawer
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
        modelRenderer.dynamicsWorld.debugDrawWorld()
        debugDrawer.end()
        modelRenderer.modelBatch.end()
    }

    override fun processSystem() { }
}
