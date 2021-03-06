package io.piotrjastrzebski.lis.game.processors

import com.artemis.*
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.artemis.utils.IntBag
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.model.MeshPart
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Disposable
import io.piotrjastrzebski.lis.game.components.Culled
import io.piotrjastrzebski.lis.game.components.ModelDef
import io.piotrjastrzebski.lis.game.components.RenderableModel
import io.piotrjastrzebski.lis.game.components.Transform
import io.piotrjastrzebski.lis.game.processors.physics.BulletContactListener
import io.piotrjastrzebski.lis.game.processors.physics.BulletMotionState
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM
import io.piotrjastrzebski.lis.utils.Assets
import io.piotrjastrzebski.lis.utils.Resizing

/**
 * Created by PiotrJ on 22/12/15.
 */
class ModelInit() : BaseEntitySystem(Aspect.all(ModelDef::class.java, Transform::class.java)) {
    @Wire lateinit var mRenderableModel: ComponentMapper<RenderableModel>
    @Wire lateinit var mModelDef: ComponentMapper<ModelDef>
    @Wire lateinit var mTransform: ComponentMapper<Transform>
    @Wire lateinit var assets: Assets


    override fun initialize() {
    }

    override fun inserted(entityId: Int) {
        val trans = mTransform.get(entityId)
        val def = mModelDef.get(entityId)
        val inst: ModelInstance? = assets.getModelInstance(def.model, def.nodeId)
        if (inst == null) {
            Gdx.app.log("", "Model ${def.model} not found!")
            return
        }
        val model = mRenderableModel.create(entityId)
        inst.transform.set(trans.transform)
        inst.calculateTransforms()
        model.instance = inst
    }

    override fun removed(entityId: Int) {

    }

    override fun processSystem() {

    }
}
