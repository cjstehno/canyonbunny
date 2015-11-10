package com.packtpub.libgdx.canyonbunny

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.packtpub.libgdx.canyonbunny.game.Assets
import com.packtpub.libgdx.canyonbunny.game.WorldController
import com.packtpub.libgdx.canyonbunny.game.WorldRenderer
import groovy.transform.TypeChecked

@TypeChecked
class CanyonBunnyMain extends ApplicationAdapter {

    private static final String TAG = CanyonBunnyMain.name

    private WorldController worldController
    private WorldRenderer worldRenderer
    private boolean paused

    @Override
    void create() {
        Gdx.app.logLevel = Application.LOG_DEBUG

        // load assets
        Assets.instance.init(newAssetManager())

        worldController = new WorldController()
        worldRenderer = new WorldRenderer(worldController)

        paused = false
    }

    @Override
    void render() {
        if (!paused) {
            worldController.update(Gdx.graphics.deltaTime)
        }

        Gdx.gl.glClearColor((0x64 / 255.0f) as float, (0x95 / 255.0f) as float, (0xed / 255.0f) as float, (0xff / 255.0f) as float)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        worldRenderer.render()
    }

    @Override
    void resize(int width, int height) {
        worldRenderer.resize(width, height)
    }

    @Override
    void dispose() {
        worldRenderer.dispose()
        Assets.instance.dispose()
    }

    @Override
    void pause() {
        paused = true
    }

    @Override
    void resume() {
        Assets.instance.init(newAssetManager())
        paused = false
    }

    private static AssetManager newAssetManager(){
        new AssetManager()
//        new AssetManager(new ClasspathFileHandleResolver())
    }
}
