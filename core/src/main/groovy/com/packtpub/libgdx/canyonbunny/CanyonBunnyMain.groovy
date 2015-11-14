package com.packtpub.libgdx.canyonbunny

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.packtpub.libgdx.canyonbunny.game.Assets
import com.packtpub.libgdx.canyonbunny.screens.MenuScreen
import groovy.transform.TypeChecked

@TypeChecked
class CanyonBunnyMain extends Game {

    @Override
    void create() {
        // Set Libgdx log level 
        Gdx.app.setLogLevel(Application.LOG_DEBUG)

        // Load assets
        Assets.instance.init(new AssetManager())

        // Start game at menu screen
        setScreen(new MenuScreen(this))
    }
}
