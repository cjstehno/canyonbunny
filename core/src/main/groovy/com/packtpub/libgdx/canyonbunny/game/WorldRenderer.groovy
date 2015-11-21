package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Disposable
import com.packtpub.libgdx.canyonbunny.util.Constants
import com.packtpub.libgdx.canyonbunny.util.GamePreferences
import groovy.transform.TypeChecked

@TypeChecked
class WorldRenderer implements Disposable {

    private static final boolean DEBUG_DRAW_BOX2D_WORLD = false

    private OrthographicCamera camera
    private OrthographicCamera cameraGui
    private SpriteBatch batch
    private WorldController worldController
    private Box2DDebugRenderer box2DDebugRenderer

    WorldRenderer(WorldController worldController) {
        this.worldController = worldController
        init()
    }

    private void init() {
        batch = new SpriteBatch()

        camera = new OrthographicCamera(Constants.VIEWPOINT_WIDTH, Constants.VIEWPOINT_HEIGHT)
        camera.position.set(0, 0, 0)
        camera.update()

        cameraGui = new OrthographicCamera(Constants.VIEWPORT_GUI_HEIGHT, Constants.VIEWPORT_GUI_WIDTH)
        cameraGui.position.set(0f, 0f, 0f)
        cameraGui.setToOrtho(true)
        cameraGui.update()

        box2DDebugRenderer = new Box2DDebugRenderer()
    }

    void render() {
        renderWorld(batch)
        renderGui(batch)
    }

    private void renderWorld(SpriteBatch batch) {
        worldController.cameraHelper.applyTo(camera)
        batch.setProjectionMatrix(camera.combined)
        batch.begin()
        worldController.level.render(batch)
        batch.end()

        if (DEBUG_DRAW_BOX2D_WORLD) {
            box2DDebugRenderer.render(worldController.b2world, camera.combined)
        }
    }

    void resize(int width, int height) {
        camera.viewportWidth = ((Constants.VIEWPOINT_HEIGHT / height) * width) as float
        camera.update()

        cameraGui.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT
        cameraGui.viewportWidth = ((Constants.VIEWPORT_GUI_HEIGHT / height) * width) as float
        cameraGui.position.set((cameraGui.viewportWidth / 2) as float, (cameraGui.viewportHeight / 2) as float, 0)
        cameraGui.update()
    }

    @Override
    void dispose() {
        batch.dispose()
    }

    private void renderGui(SpriteBatch batch) {
        batch.setProjectionMatrix(cameraGui.combined)
        batch.begin()

        // draw collected gold coins icon + text (anchored to top left edge)
        renderGuiScore(batch)

        // draw feather icon
        renderGuiFeatherPowerup(batch)

        // draw extra lives icon + text (anchored to top right edge)
        renderGuiExtraLive(batch)

        // draw FPS text (anchored to bottom right edge)
        if (GamePreferences.instance.showFpsCounter) {
            renderGuiFpsCounter(batch)
        }

        renderGuiGameOverMessage(batch)

        batch.end()
    }

    private void renderGuiScore(SpriteBatch batch) {
        float x = -15
        float y = -15
        float offsetX = 50
        float offsetY = 50
        if (worldController.scoreVisual < worldController.score) {
            long shakeAlpha = System.currentTimeMillis() % 360
            float shakeDist = 1.5f
            offsetX += MathUtils.sinDeg((shakeAlpha * 2.2f) as float) * shakeDist
            offsetY += MathUtils.sinDeg((shakeAlpha * 2.9f) as float) * shakeDist
        }
        batch.draw(Assets.instance.goldCoin.goldCoin, x, y, offsetX, offsetY, 100, 100, 0.35f, -0.35f, 0)
        Assets.instance.fonts.defaultBig.draw(batch, "${worldController.scoreVisual as int}", (x + 75) as float, (y + 37) as float)
    }

    private void renderGuiExtraLive(SpriteBatch batch) {
        float x = cameraGui.viewportWidth - 50 - Constants.LIVES_START * 50
        float y = -15f

        for (int i = 0; i < Constants.LIVES_START; i++) {
            if (worldController.lives <= i) {
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            }

            batch.draw(Assets.instance.bunny.head, x + i * 50 as float, y, 50, 50, 120, 100, 0.35f, -0.35f, 0)
            batch.setColor(1, 1, 1, 1)
        }

        if (worldController.lives >= 0 && worldController.livesVisual > worldController.lives) {
            int i = worldController.lives
            float alphaColor = Math.max(0, worldController.livesVisual - worldController.lives - 0.5f)
            float alphaScale = 0.35f * (2 + worldController.lives - worldController.livesVisual) * 2
            float alphaRotate = -45 * alphaColor
            batch.setColor(1.0f, 0.7f, 0.7f, alphaColor)
            batch.draw(Assets.instance.bunny.head, (x + i * 50) as float, y, 50, 50, 120, 100, alphaScale, -alphaScale, alphaRotate)
            batch.setColor(1, 1, 1, 1)
        }
    }

    private void renderGuiFpsCounter(SpriteBatch batch) {
        float x = cameraGui.viewportWidth - 55
        float y = cameraGui.viewportHeight - 15
        int fps = Gdx.graphics.framesPerSecond

        BitmapFont fpsFont = Assets.instance.fonts.defaultNormal

        if (fps >= 45) {
            // 45 or more FPS show up in green
            fpsFont.setColor(0, 1, 0, 1)

        } else if (fps >= 30) {
            // 30 or more FPS show up in yellow
            fpsFont.setColor(1, 1, 0, 1)

        } else {
            // less than 30 FPS show up in red
            fpsFont.setColor(1, 0, 0, 1)

        }

        fpsFont.draw(batch, "FPS: $fps", x, y)
        fpsFont.setColor(1, 1, 1, 1) // white
    }

    private void renderGuiGameOverMessage(SpriteBatch batch) {
        float x = (cameraGui.viewportWidth / 2) as float
        float y = (cameraGui.viewportHeight / 2) as float
        if (worldController.isGameOver()) {
            BitmapFont fontGameOver = Assets.instance.fonts.defaultBig
            fontGameOver.setColor(1, 0.75f, 0.25f, 1)
            fontGameOver.draw(batch, 'GAME OVER', x, y)
            fontGameOver.setColor(1, 1, 1, 1)
        }
    }

    private void renderGuiFeatherPowerup(SpriteBatch batch) {
        float x = -15
        float y = 30
        float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup
        if (timeLeftFeatherPowerup > 0) {
            // Start icon fade in/out if the left power-up time is less than 4 seconds. The fade interval is set
            // to 5 changes per second.

            if (timeLeftFeatherPowerup < 4) {
                if (((int) (timeLeftFeatherPowerup * 5) % 2) != 0) {
                    batch.setColor(1, 1, 1, 0.5f)
                }
            }

            batch.draw(Assets.instance.feather.feather, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0)
            batch.setColor(1, 1, 1, 1)
            Assets.instance.fonts.defaultSmall.draw(batch, timeLeftFeatherPowerup as String, (x + 60) as float, (y + 57) as float)
        }
    }
}
