package com.packtpub.libgdx.canyonbunny.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.packtpub.libgdx.canyonbunny.game.WorldController
import com.packtpub.libgdx.canyonbunny.game.WorldRenderer

class GameScreen extends AbstractGameScreen {

    private static final String TAG = GameScreen.name

    private WorldController worldController
    private WorldRenderer worldRenderer

    private boolean paused

    GameScreen(Game game) {
        super(game)
    }

    @Override
    void render(float deltaTime) {
        // Do not update game world when paused.
        if (!paused) {
            // Update game world by the time that has passed since last rendered frame.
            worldController.update(deltaTime)
        }

        // Sets the clear screen color to: Cornflower Blue
        Gdx.gl.glClearColor((0x64 / 255.0f) as float, (0x95 / 255.0f) as float, (0xed / 255.0f) as float, (0xff / 255.0f) as float)

        // Clears the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // Render game world to screen
        worldRenderer.render()
    }

    @Override
    void resize(int width, int height) {
        worldRenderer.resize(width, height)
    }

    @Override
    void show() {
        worldController = new WorldController(game)
        worldRenderer = new WorldRenderer(worldController)
        Gdx.input.setCatchBackKey(true)
    }

    @Override
    void hide() {
        worldRenderer.dispose()
        Gdx.input.setCatchBackKey(false)
    }

    @Override
    void pause() {
        paused = true
    }

    @Override
    void resume() {
        super.resume()
        // Only called on Android!
        paused = false
    }
}
