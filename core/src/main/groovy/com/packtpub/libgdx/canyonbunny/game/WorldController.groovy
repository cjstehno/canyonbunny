package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.Disposable
import com.packtpub.libgdx.canyonbunny.game.objects.*
import com.packtpub.libgdx.canyonbunny.screens.DirectedGame
import com.packtpub.libgdx.canyonbunny.screens.MenuScreen
import com.packtpub.libgdx.canyonbunny.screens.transitions.ScreenTransitionSlide
import com.packtpub.libgdx.canyonbunny.util.AudioManager
import com.packtpub.libgdx.canyonbunny.util.CameraHelper
import com.packtpub.libgdx.canyonbunny.util.Constants
import groovy.transform.TypeChecked

import static com.badlogic.gdx.Input.Keys.*
import static com.packtpub.libgdx.canyonbunny.screens.transitions.ScreenTransitionSlide.DOWN

@TypeChecked
class WorldController extends InputAdapter implements Disposable {

    private static final String TAG = WorldController.name

    CameraHelper cameraHelper
    Level level
    int lives
    int score
    float livesVisual
    float scoreVisual
    World b2world

    private Rectangle r1 = new Rectangle()
    private Rectangle r2 = new Rectangle()
    private float timeLeftGameOverDelay
    private DirectedGame game
    private boolean goalReached

    WorldController(DirectedGame game) {
        this.game = game
        init()
    }

    private void init() {
        cameraHelper = new CameraHelper()

        lives = Constants.LIVES_START
        livesVisual = lives
        timeLeftGameOverDelay = 0

        initLevel()
    }

    @Override
    void dispose() {
        if (b2world) b2world.dispose()
    }

    private void initPhysics() {
        if (b2world != null) b2world.dispose()

        b2world = new World(new Vector2(0, -9.81f), true)

        // Rocks
        Vector2 origin = new Vector2()
        level.rocks.each { Rock rock ->
            BodyDef bodyDef = new BodyDef()
            bodyDef.type = BodyDef.BodyType.KinematicBody
            bodyDef.position.set(rock.position)
            Body body = b2world.createBody(bodyDef)
            rock.body = body
            PolygonShape polygonShape = new PolygonShape()
            origin.x = (rock.bounds.width / 2.0f) as float
            origin.y = (rock.bounds.height / 2.0f) as float
            polygonShape.setAsBox((rock.bounds.width / 2.0f) as float, (rock.bounds.height / 2.0f) as float, origin, 0)
            FixtureDef fixtureDef = new FixtureDef()
            fixtureDef.shape = polygonShape
            body.createFixture(fixtureDef)
            polygonShape.dispose()
        }
    }

    private void spawnCarrots(Vector2 pos, int numCarrots, float radius) {
        float carrotShapeScale = 0.5f

        // create carrots with box2d body and fixture
        for (int i = 0; i < numCarrots; i++) {
            Carrot carrot = new Carrot()

            // calculate random spawn position, rotation, and scale
            float x = MathUtils.random(-radius, radius)
            float y = MathUtils.random(5.0f, 15.0f)
            float rotation = MathUtils.random(0.0f, 360.0f) * MathUtils.degreesToRadians
            float carrotScale = MathUtils.random(0.5f, 1.5f)
            carrot.scale.set(carrotScale, carrotScale)

            // create box2d body for carrot with start position// and angle of rotation
            BodyDef bodyDef = new BodyDef()
            bodyDef.position.set(pos)
            bodyDef.position.add(x, y)
            bodyDef.angle = rotation
            Body body = b2world.createBody(bodyDef)
            body.setType(BodyDef.BodyType.DynamicBody)
            carrot.body = body

            // create rectangular shape for carrot to allow// interactions (collisions) with other objects
            PolygonShape polygonShape = new PolygonShape()
            float halfWidth = (carrot.bounds.width / 2.0f * carrotScale) as float
            float halfHeight = (carrot.bounds.height / 2.0f * carrotScale) as float
            polygonShape.setAsBox((halfWidth * carrotShapeScale) as float, (halfHeight * carrotShapeScale) as float)

            // set physics attributes
            FixtureDef fixtureDef = new FixtureDef()
            fixtureDef.shape = polygonShape
            fixtureDef.density = 50
            fixtureDef.restitution = 0.5f
            fixtureDef.friction = 0.5f
            body.createFixture(fixtureDef)
            polygonShape.dispose()

            // finally, add new carrot to list for updating/rendering
            level.carrots.add(carrot)
        }
    }

    private void onCollisionBunnyWithGoal() {
        goalReached = true
        timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED
        Vector2 centerPosBunnyHead = new Vector2(level.bunnyHead.position)
        centerPosBunnyHead.x += level.bunnyHead.bounds.width
        spawnCarrots(centerPosBunnyHead, Constants.CARROTS_SPAWN_MAX, Constants.CARROTS_SPAWN_RADIUS)
    }

    private void backToMenu() {
        game.setScreen(
            new MenuScreen(game),
            ScreenTransitionSlide.init(0.75f, DOWN, false, Interpolation.bounceOut)
        )
    }

    boolean isGameOver() {
        lives < 0
    }

    boolean isPlayerInWater() {
        level.bunnyHead.position.y < -5
    }

    private void initLevel() {
        score = 0
        scoreVisual = score
        level = new Level(Constants.LEVEL_01)
        initPhysics()
        cameraHelper.target = level.bunnyHead
    }

    void update(float deltaTime) {
        handleDebugInput(deltaTime)

        if (isGameOver() || goalReached) {
            timeLeftGameOverDelay -= deltaTime
            if (timeLeftGameOverDelay < 0) backToMenu()

        } else {
            handleInputGame(deltaTime)
        }

        level.update(deltaTime)
        testCollisions()
        b2world.step(deltaTime, 8, 3)
        cameraHelper.update(deltaTime)

        if (!isGameOver() && isPlayerInWater()) {
            AudioManager.instance.play(Assets.instance.sounds.liveLost)
            lives--
            if (isGameOver()) {
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER
            } else {
                initLevel()
            }
        }
        level.mountains.updateScrollPosition(cameraHelper.position)

        if (livesVisual > lives) {
            livesVisual = Math.max(lives, livesVisual - 1 * deltaTime)
        }

        if (scoreVisual < score) {
            scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime)
        }
    }

    private void handleDebugInput(float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return

        if (!cameraHelper.hasTarget(level.bunnyHead)) {
            // Camera Controls (move)
            float camMoveSpeed = 5 * deltaTime
            float camMoveSpeedAccelerationFactor = 5
            if (Gdx.input.isKeyPressed(SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor
            if (Gdx.input.isKeyPressed(LEFT)) moveCamera(-camMoveSpeed, 0)
            if (Gdx.input.isKeyPressed(RIGHT)) moveCamera(camMoveSpeed, 0)
            if (Gdx.input.isKeyPressed(UP)) moveCamera(0, camMoveSpeed)
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) moveCamera(0, -camMoveSpeed)
            if (Gdx.input.isKeyPressed(BACKSPACE)) cameraHelper.setPosition(0, 0)
        }

        // Camera Controls (zoom)
        float camZoomSpeed = 1 * deltaTime
        float camZoomSpeedAccelerationFactor = 5
        if (Gdx.input.isKeyPressed(SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor
        if (Gdx.input.isKeyPressed(COMMA)) cameraHelper.addZoom(camZoomSpeed)
        if (Gdx.input.isKeyPressed(PERIOD)) cameraHelper.addZoom(-camZoomSpeed)
        if (Gdx.input.isKeyPressed(SLASH)) cameraHelper.setZoom(1)
    }

    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x
        y += cameraHelper.getPosition().y
        cameraHelper.setPosition(x, y)
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == R) {
            // Reset game world
            init()

            Gdx.app.debug TAG, 'Game world has been reset'

        } else if (keycode == ENTER) {
            // Toggle camera follow
            cameraHelper.target = cameraHelper.hasTarget() ? null : level.bunnyHead

            Gdx.app.debug TAG, "Camera follow enabled: ${cameraHelper.hasTarget()}"

        } else if (keycode == ESCAPE || keycode == BACK) {
            backToMenu()
        }

        return false
    }

    private void handleInputGame(float deltaTime) {
        if (cameraHelper.hasTarget(level.bunnyHead)) {
            // Player Movement
            if (Gdx.input.isKeyPressed(LEFT)) {
                level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x

            } else if (Gdx.input.isKeyPressed(RIGHT)) {
                level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x

            } else {
                // Execute auto-forward movement on non-desktop platform
                if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x
                }
            }

            // Bunny Jump
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(SPACE)) {
                level.bunnyHead.setJumping(true)
            } else {
                level.bunnyHead.setJumping(false)
            }
        }
    }

    private void testCollisions() {
        r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width, level.bunnyHead.bounds.height)

        // Test collision: Bunny Head <-> Rocks
        for (Rock rock : level.rocks) {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height)

            if (!r1.overlaps(r2)) continue

            onCollisionBunnyWithRock(rock)
            // IMPORTANT: must do all collisions for valid edge testing on rocks.
        }

        // Test collision: Bunny Head <-> Gold Coins
        for (GoldCoin goldcoin : level.goldCoins) {
            if (goldcoin.collected) continue

            r2.set(goldcoin.position.x, goldcoin.position.y, goldcoin.bounds.width, goldcoin.bounds.height)

            if (!r1.overlaps(r2)) continue

            onCollisionBunnyWithGoldCoin(goldcoin)

            break
        }

        // Test collision: Bunny Head <-> Feathers
        for (Feather feather : level.feathers) {
            if (feather.collected) continue

            r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height)

            if (!r1.overlaps(r2)) continue

            onCollisionBunnyWithFeather(feather)

            break
        }

        // Test collision: Bunny Head <-> Goal
        if (!goalReached) {
            r2.set(level.goal.bounds)
            r2.x += level.goal.position.x
            r2.y += level.goal.position.y
            if (r1.overlaps(r2)) onCollisionBunnyWithGoal()
        }
    }

    private void onCollisionBunnyWithRock(Rock rock) {
        BunnyHead bunnyHead = level.bunnyHead

        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height))

        if (heightDifference > 0.25f) {
            boolean hitRightEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f)
            if (hitRightEdge) {
                bunnyHead.position.x = rock.position.x + rock.bounds.width
            } else {
                bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width
            }
            return
        }

        switch (bunnyHead.jumpState) {
            case BunnyHead.JUMP_STATE.GROUNDED:
                break

            case BunnyHead.JUMP_STATE.FALLING:
            case BunnyHead.JUMP_STATE.JUMP_FALLING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y
                bunnyHead.jumpState = BunnyHead.JUMP_STATE.GROUNDED
                break

            case BunnyHead.JUMP_STATE.JUMP_RISING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y
                break
        }
    }

    private void onCollisionBunnyWithGoldCoin(GoldCoin goldCoin) {
        goldCoin.collected = true

        AudioManager.instance.play(Assets.instance.sounds.pickupCoin)

        score += goldCoin.score

        Gdx.app.log TAG, 'Gold coin collected'
    }

    private void onCollisionBunnyWithFeather(Feather feather) {
        feather.collected = true

        AudioManager.instance.play(Assets.instance.sounds.pickupFeather)

        score += feather.score

        level.bunnyHead.setFeatherPowerup(true)

        Gdx.app.log TAG, 'Feather collected'
    }
}
