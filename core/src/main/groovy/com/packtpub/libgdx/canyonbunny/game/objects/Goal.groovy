package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.packtpub.libgdx.canyonbunny.game.Assets
import groovy.transform.TypeChecked

@TypeChecked
class Goal extends AbstractGameObject {

    private TextureRegion regGoal

    Goal() {
        init()
    }

    private void init() {
        dimension.set(3.0f, 3.0f)
        regGoal = Assets.instance.levelDecoration.goal

        // Set bounding box for collision detection
        bounds.set(1, Float.MIN_VALUE, 10, Float.MAX_VALUE)
        origin.set((dimension.x / 2.0f) as float, 0.0f)
    }

    public void render(SpriteBatch batch) {
        batch.draw(regGoal.texture, (position.x - origin.x) as float, (position.y - origin.y) as float, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, regGoal.regionX, regGoal.regionY, regGoal.regionWidth, regGoal.regionHeight, false, false)
    }
}