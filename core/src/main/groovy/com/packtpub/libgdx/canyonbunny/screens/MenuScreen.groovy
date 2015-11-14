package com.packtpub.libgdx.canyonbunny.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

class MenuScreen extends AbstractGameScreen {

    private static final String TAG = MenuScreen.name

    MenuScreen(Game game) {
        super(game)
    }

    @Override
    void render(float deltaTime) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if(Gdx.input.isTouched()){
            game.setScreen(new GameScreen(game))
        }
    }

    @Override
    void resize(int width, int height) {

    }

    @Override
    void show() {

    }

    @Override
    void hide() {

    }

    @Override
    void pause() {

    }
}
