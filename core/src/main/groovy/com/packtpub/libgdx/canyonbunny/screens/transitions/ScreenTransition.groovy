package com.packtpub.libgdx.canyonbunny.screens.transitions

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import groovy.transform.TypeChecked

@TypeChecked
interface ScreenTransition {

    float getDuration()

    void render(SpriteBatch batch, Texture currScreen, Texture nextScreen, float alpha)
}
