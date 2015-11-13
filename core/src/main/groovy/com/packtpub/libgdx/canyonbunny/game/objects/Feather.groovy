package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.packtpub.libgdx.canyonbunny.game.Assets

class Feather extends AbstractGameObject {

    boolean collected

    private TextureRegion regFeather

    Feather() {
        init()
    }

    private void init() {
        dimension.set(0.5f, 0.5f)

        regFeather = Assets.instance.feather.feather

        bounds.set(0f, 0f, dimension.x, dimension.y)

        collected = false
    }

    @Override
    void render(SpriteBatch batch) {
        if (!collected) {
            batch.draw(regFeather.texture, position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, regFeather.regionX, regFeather.regionY, regFeather.regionWidth, regFeather.regionHeight, false, false)
        }
    }

    int getScore() {
        return 250
    }
}
