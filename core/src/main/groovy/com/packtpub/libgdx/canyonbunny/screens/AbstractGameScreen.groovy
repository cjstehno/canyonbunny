package com.packtpub.libgdx.canyonbunny.screens

import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.Screen
import com.badlogic.gdx.assets.AssetManager
import com.packtpub.libgdx.canyonbunny.game.Assets
import groovy.transform.TypeChecked

@TypeChecked
abstract class AbstractGameScreen implements Screen {

    protected DirectedGame game

    AbstractGameScreen(DirectedGame game) {
        this.game = game
    }

    abstract void render(float deltaTime)

    abstract void resize(int width, int height)

    abstract void show()

    abstract void hide()

    abstract void pause()

    void resume() {
        Assets.instance.init(new AssetManager())
    }

    void dispose() {
        Assets.instance.dispose()
    }

    abstract InputProcessor getInputProcessor()
}
