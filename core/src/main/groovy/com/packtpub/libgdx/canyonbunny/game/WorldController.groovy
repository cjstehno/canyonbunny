package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Rectangle
import com.packtpub.libgdx.canyonbunny.game.objects.BunnyHead
import com.packtpub.libgdx.canyonbunny.game.objects.Feather
import com.packtpub.libgdx.canyonbunny.game.objects.GoldCoin
import com.packtpub.libgdx.canyonbunny.game.objects.Rock
import com.packtpub.libgdx.canyonbunny.util.CameraHelper
import com.packtpub.libgdx.canyonbunny.util.Constants
import groovy.transform.TypeChecked

import static com.badlogic.gdx.Input.Keys.*

@TypeChecked
class WorldController extends InputAdapter {

    private static final String TAG = WorldController.name

    CameraHelper cameraHelper
    Level level
    int lives
    int score

    private Rectangle r1 = new Rectangle()
    private Rectangle r2 = new Rectangle()

    WorldController() {
        init()
    }

    private void init() {
        Gdx.input.inputProcessor = this
        cameraHelper = new CameraHelper()

        lives = Constants.LIVES_START
        initLevel()
    }

    private void initLevel() {
        score = 0
        level = new Level(Constants.LEVEL_01)
        cameraHelper.target = level.bunnyHead
    }

    void update(float deltaTime) {
        handleDebugInput(deltaTime)
        handleInputGame(deltaTime)
        level.update(deltaTime)
        testCollisions()
        cameraHelper.update(deltaTime)
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
            if (Gdx.input.isKeyPressed(DOWN)) moveCamera(0, -camMoveSpeed)
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
        }

        return false
    }

    private void handleInputGame (float deltaTime) {
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

        score += goldCoin.score

        Gdx.app.log TAG, 'Gold coin collected'
    }

    private void onCollisionBunnyWithFeather(Feather feather) {
        feather.collected = true

        score += feather.score

        level.bunnyHead.setFeatherPowerup(true)

        Gdx.app.log TAG, 'Feather collected'
    }
}
