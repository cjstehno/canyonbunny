package com.packtpub.libgdx.canyonbunny.screens.transitions

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.utils.Array

class ScreenTransitionSlice implements ScreenTransition {

    static final int UP = 1
    static final int DOWN = 2
    static final int UP_DOWN = 3

    private static final ScreenTransitionSlice instance = new ScreenTransitionSlice()

    float duration
    private int direction
    private Interpolation easing
    private Array<Integer> sliceIndex = new Array<Integer>()

    static ScreenTransitionSlice init(float duration, int direction, int numSlices, Interpolation easing) {
        instance.duration = duration
        instance.direction = direction
        instance.easing = easing

        // create shuffled list of slice indices which determines the order of slice animation
        instance.sliceIndex.clear()

        for (int i = 0; i < numSlices; i++)
            instance.sliceIndex.add(i)

        instance.sliceIndex.shuffle()

        return instance
    }

    @Override
    public void render(SpriteBatch batch, Texture currScreen, Texture nextScreen, float alpha) {
        float w = currScreen.width
        float h = currScreen.height
        float x = 0
        float y = 0
        int sliceWidth = (int) (w / sliceIndex.size)

        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch.begin()
        batch.draw(currScreen, 0, 0, 0, 0, w, h, 1, 1, 0, 0, 0, currScreen.width, currScreen.height, false, true)

        if (easing != null) alpha = easing.apply(alpha)

        for (int i = 0; i < sliceIndex.size; i++) {
            // current slice/column
            x = i * sliceWidth

            // vertical displacement using randomized list of slice indices
            float offsetY = h * (1 + sliceIndex.get(i) / (float) sliceIndex.size)

            switch (direction) {
                case UP:
                    y = (-offsetY + offsetY * alpha) as float
                    break

                case DOWN:
                    y = (offsetY - offsetY * alpha) as float
                    break

                case UP_DOWN:
                    if (i % 2 == 0) {
                        y = (-offsetY + offsetY * alpha) as float
                    } else {
                        y = (offsetY - offsetY * alpha) as float
                    }
                    break

            }

            batch.draw(nextScreen, x, y, 0, 0, sliceWidth, h, 1, 1, 0, i * sliceWidth, 0, sliceWidth, nextScreen.height, false, true)
        }

        batch.end()
    }
}
