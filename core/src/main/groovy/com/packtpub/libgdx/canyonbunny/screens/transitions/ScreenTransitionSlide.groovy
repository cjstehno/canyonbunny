package com.packtpub.libgdx.canyonbunny.screens.transitions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import groovy.transform.TypeChecked

@TypeChecked
class ScreenTransitionSlide implements ScreenTransition {

    static final int LEFT = 1
    static final int RIGHT = 2
    static final int UP = 3
    static final int DOWN = 4

    private static final ScreenTransitionSlide instance = new ScreenTransitionSlide()

    float duration
    private int direction
    private boolean slideOut
    private Interpolation easing

    static ScreenTransitionSlide init(float duration, int direction, boolean slideOut, Interpolation easing) {
        instance.duration = duration
        instance.direction = direction
        instance.slideOut = slideOut
        instance.easing = easing
        return instance
    }

    @Override
    public void render(SpriteBatch batch, Texture currScreen, Texture nextScreen, float alpha) {
        float w = currScreen.width
        float h = currScreen.height
        float x = 0
        float y = 0
        if (easing != null) alpha = easing.apply(alpha)

        // calculate position offset
        switch (direction) {
            case LEFT:
                x = (-w * alpha) as float
                if (!slideOut) x += w
                break

            case RIGHT:
                x = (w * alpha) as float
                if (!slideOut) x -= w
                break

            case UP:
                y = (h * alpha) as float
                if (!slideOut) y -= h
                break

            case DOWN:
                y = (-h * alpha) as float
                if (!slideOut) y += h
                break
        }

        // drawing order depends on slide type ('in' or 'out')
        Texture texBottom = slideOut ? nextScreen : currScreen
        Texture texTop = slideOut ? currScreen : nextScreen

        // finally, draw both screens
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        batch.begin()
        batch.draw(texBottom, 0, 0, 0, 0, w, h, 1, 1, 0, 0, 0, currScreen.width, currScreen.height, false, true)
        batch.draw(texTop, x, y, 0, 0, w, h, 1, 1, 0, 0, 0, nextScreen.width, nextScreen.height, false, true)
        batch.end()
    }
}
