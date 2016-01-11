package io.piotrjastrzebski.lis.game.processors

import com.artemis.*
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.VertexAttributes.Usage
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.bullet.collision.*
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups
import com.badlogic.gdx.physics.bullet.dynamics.btKinematicCharacterController
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody
import io.piotrjastrzebski.lis.game.components.*
import io.piotrjastrzebski.lis.game.components.physics.BulletBody
import io.piotrjastrzebski.lis.game.create
import io.piotrjastrzebski.lis.game.createAndEdit
import io.piotrjastrzebski.lis.game.processors.physics.BulletMotionState
import io.piotrjastrzebski.lis.game.processors.physics.Physics
import io.piotrjastrzebski.lis.screens.WIRE_GAME_CAM

/**
 * Created by EvilEntity on 22/12/2015.
 */
class PlayerSpawner : BaseEntitySystem(Aspect.all(Player::class.java, Transform::class.java)) {
    @Wire(name = WIRE_GAME_CAM) lateinit var camera: OrthographicCamera
    @Wire lateinit var keybinds: KeyBindings
    @Wire lateinit var physics: Physics
    @Wire lateinit var mTransform: ComponentMapper<Transform>
    @Wire lateinit var mPlayer: ComponentMapper<Player>
    @Wire lateinit var mRenderableModel: ComponentMapper<RenderableModel>
    val model:Model
    init {
        val mb = ModelBuilder()

        val material = Material(ColorAttribute.createDiffuse(Color.RED))
        val attributes = Usage.Position or Usage.Normal;
        model = mb.createCapsule(0.35f, 1.5f, 8, material, attributes.toLong())
    }

    override fun initialize() {
//        keybinds.register(moveKeys, cbDown, cbUp)
        // todo some hacks for testing
//        keybinds.register(Keys.F1, {toggle()}, {false})
//        isEnabled = false

        spawnPlayer()
    }

    lateinit var playerTrans: Matrix4
    lateinit var ghostObject: btPairCachingGhostObject
    lateinit var  characterController: btKinematicCharacterController
    private fun spawnPlayer() {
        val e = world.createAndEdit()
        e.create<Player>()
        val trans = e.create<Transform>()
        trans.transform.setFromEulerAngles(0f, 90f, 0f)
        trans.transform.trn(3f, 3f, 1.25f)
        // TODO use debug renderable model
        val renderable = e.create<RenderableModel>()
        renderable.instance = createPlayerModel()
        renderable.instance.transform.set(trans.transform)
        playerTrans = renderable.instance.transform

//        e.create<CameraFollower>()
        e.create<Dynamic>()

        ghostObject = btPairCachingGhostObject();
        ghostObject.worldTransform = playerTrans;
        val ghostShape = btCapsuleShape(.35f, .75f);
        ghostObject.collisionShape = ghostShape;
        ghostObject.collisionFlags = btCollisionObject.CollisionFlags.CF_CHARACTER_OBJECT;
        characterController = btKinematicCharacterController(ghostObject, ghostShape, .35f);
        physics.btWorld.addCollisionObject(ghostObject,
                CollisionFilterGroups.CharacterFilter.toShort(),
                (CollisionFilterGroups.StaticFilter or CollisionFilterGroups.DefaultFilter).toShort()
        )
        physics.btWorld.addAction(characterController)
        characterController.setUpAxis(2)
    }

    private fun createPlayerModel(): ModelInstance {
        return ModelInstance(model)
    }

    override fun dispose() {
        model.dispose()
    }


    private val characterDirection = Vector3()
    private val walkDirection = Vector3()

    override fun processSystem() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerTrans.rotate(0f, 1f, 0f, 5f);
            ghostObject.setWorldTransform(playerTrans);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerTrans.rotate(0f, 1f, 0f, -5f);
            ghostObject.setWorldTransform(playerTrans);
        }
        // Fetch which direction the character is facing now
        characterDirection.set(-1f, 0f, 0f).rot(playerTrans).nor();
        // Set the walking direction accordingly (either forward or backward)
        walkDirection.set(0f, 0f, 0f);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            walkDirection.add(characterDirection);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            walkDirection.add(-characterDirection.x, -characterDirection.y, -characterDirection.z);
        walkDirection.scl(4f * Gdx.graphics.getDeltaTime());
        // And update the character controller
        characterController.setWalkDirection(walkDirection);
        // And fetch the new transformation of the character (this will make the model be rendered correctly)
        ghostObject.getWorldTransform(playerTrans);
    }

    override fun removed(entityId: Int) {
        // queue next spawn or whatever
    }
}

