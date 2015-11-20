package com.packtpub.libgdx.canyonbunny.screens.transitions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import groovy.transform.TypeChecked

@TypeChecked
class ScreenTransitionFade implements ScreenTransition {

    private static final ScreenTransitionFade instance = new ScreenTransitionFade()
    float duration

    static ScreenTransitionFade init(float duration) {
        instance.duration = duration
        instance
    }

    @Override
    public void render(SpriteBatch batch, Texture currScreen, Texture nextScreen, float alpha) {
        float w = currScreen.width
        float h = currScreen.height
        alpha = Interpolation.fade.apply(alpha)

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        batch.setColor(1, 1, 1, 1)
        batch.draw(currScreen, 0, 0, 0, 0, w, h, 1, 1, 0, 0, 0, currScreen.width, currScreen.height, false, true)
        batch.setColor(1, 1, 1, alpha)
        batch.draw(nextScreen, 0, 0, 0, 0, w, h, 1, 1, 0, 0, 0, nextScreen.width, nextScreen.height, false, true)
        batch.end()
    }
}
