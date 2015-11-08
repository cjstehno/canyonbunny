package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.packtpub.libgdx.canyonbunny.util.CameraHelper
import groovy.transform.TypeChecked

import static com.badlogic.gdx.Input.Keys.*
import static com.badlogic.gdx.math.MathUtils.random

@TypeChecked
class WorldController extends InputAdapter {

    private static final String TAG = WorldController.name

    Sprite[] testSprites
    int selectedSprite
    CameraHelper cameraHelper

    WorldController() {
        init()
    }

    private void init() {
        Gdx.input.inputProcessor = this
        cameraHelper = new CameraHelper()
        initTestObjects()
    }

    void update(float deltaTime) {
        handleDebugInput(deltaTime)
        updateTestObjects(deltaTime)
        cameraHelper.update(deltaTime)
    }

    private void handleDebugInput(float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return

        // Selected Sprite Controls
        float sprMoveSpeed = 5 * deltaTime
        if (Gdx.input.isKeyPressed(A)) moveSelectedSprite(-sprMoveSpeed, 0)
        if (Gdx.input.isKeyPressed(D)) moveSelectedSprite(sprMoveSpeed, 0)
        if (Gdx.input.isKeyPressed(W)) moveSelectedSprite(0, sprMoveSpeed)
        if (Gdx.input.isKeyPressed(S)) moveSelectedSprite(0, -sprMoveSpeed)

        // Camera Controls (move)
        float camMoveSpeed = 5 * deltaTime
        float camMoveSpeedAccelerationFactor = 5
        if (Gdx.input.isKeyPressed(SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor
        if (Gdx.input.isKeyPressed(LEFT)) moveCamera(-camMoveSpeed, 0)
        if (Gdx.input.isKeyPressed(RIGHT)) moveCamera(camMoveSpeed, 0)
        if (Gdx.input.isKeyPressed(UP)) moveCamera(0, camMoveSpeed)
        if (Gdx.input.isKeyPressed(DOWN)) moveCamera(0, -camMoveSpeed)
        if (Gdx.input.isKeyPressed(BACKSPACE)) cameraHelper.setPosition(0, 0)

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

    private void moveSelectedSprite(float x, float y) {
        testSprites[selectedSprite].translate(x, y)
    }

    private void updateTestObjects(float deltaTime) {
        // Get current rotation from selected sprite
        float rotation = testSprites[selectedSprite].rotation

        // Rotate sprite by 90 degrees per second
        rotation += 90 * deltaTime

        // Wrap around at 360 degrees
        rotation %= 360

        // Set new rotation value to selected sprite
        testSprites[selectedSprite].rotation = rotation
    }

    private void initTestObjects() {
        testSprites = new Sprite[5]

        // Create empty POT-sized Pixmap with 8 bit RGBA pixel data
        def (int width, int height) = [32, 32]

        Texture texture = new Texture(createProceduralPixmap(width, height))

        // Create new sprites using the texture
        for (int i = 0; i < testSprites.length; i++) {
            Sprite spr = new Sprite(texture)
            spr.setSize(1, 1)

            // Set origin to sprite's center
            spr.setOrigin((spr.width / 2.0f) as float, (spr.height / 2.0f) as float)

            // Calculate random position for sprite
            spr.setPosition(random(-2.0f, 2.0f), random(-2.0f, 2.0f))

            testSprites[i] = spr
        }

        // Set first sprite as selected one
        selectedSprite = 0
    }

    private Pixmap createProceduralPixmap(int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888)
        pixmap.setColor(1, 0, 0, 0.5f)
        pixmap.fill()

        pixmap.setColor(1, 1, 0, 1)
        pixmap.drawLine(0, 0, width, height)
        pixmap.drawLine(width, 0, 0, height)

        pixmap.setColor(0, 1, 1, 1)
        pixmap.drawRectangle(0, 0, width, height)

        return pixmap
    }

    @Override
    boolean keyUp(int keycode) {
        // Reset game world
        if (keycode == R) {
            init()
            Gdx.app.debug TAG, 'Game world has been reset.'

        } else if (keycode == SPACE) {
            selectedSprite = (selectedSprite + 1) % testSprites.length

            if (cameraHelper.hasTarget()) {
                cameraHelper.target = testSprites[selectedSprite]
            }

            Gdx.app.debug TAG, "Sprite #${selectedSprite} selected"

        } else if (keycode == ENTER) {
            cameraHelper.target = cameraHelper.hasTarget() ? null : testSprites[selectedSprite]

            Gdx.app.debug TAG, "Camera follow enabled: ${cameraHelper.hasTarget()}"
        }

        false
    }
}
