package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.graphics.Pixmap
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
    }

    void update(float deltaTime) {
        handleDebugInput(deltaTime)
        cameraHelper.update(deltaTime)
    }

    private void handleDebugInput(float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return

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
    public boolean keyUp(int keycode) {
        // Reset game world
        if (keycode == R) {
            init()

            Gdx.app.debug TAG, 'Game world has been reset'
        }

        return false
    }
}
