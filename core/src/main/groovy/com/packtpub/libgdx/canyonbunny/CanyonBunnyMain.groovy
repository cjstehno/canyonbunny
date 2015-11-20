package com.packtpub.libgdx.canyonbunny

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.Interpolation
import com.packtpub.libgdx.canyonbunny.game.Assets
import com.packtpub.libgdx.canyonbunny.screens.DirectedGame
import com.packtpub.libgdx.canyonbunny.screens.MenuScreen
import com.packtpub.libgdx.canyonbunny.screens.transitions.ScreenTransitionSlice
import groovy.transform.TypeChecked

import static com.packtpub.libgdx.canyonbunny.screens.transitions.ScreenTransitionSlice.UP_DOWN

@TypeChecked
class CanyonBunnyMain extends DirectedGame {

    @Override
    void create() {
        // Set Libgdx log level 
        Gdx.app.setLogLevel(Application.LOG_DEBUG)

        // Load assets
        Assets.instance.init(new AssetManager())

        // Start game at menu screen
        setScreen(new MenuScreen(this), ScreenTransitionSlice.init(2f, UP_DOWN, 10, Interpolation.pow5Out))
    }
}
