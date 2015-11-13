package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.packtpub.libgdx.canyonbunny.game.Assets

class GoldCoin extends AbstractGameObject {

    boolean collected

    private TextureRegion regGoldCoin

    GoldCoin() {
        init()
    }

    private void init() {
        dimension.set(0.5f, 0.5f)

        regGoldCoin = Assets.instance.goldCoin.goldCoin

        bounds.set(0f, 0f, dimension.x, dimension.y)

        collected = false
    }

    @Override
    void render(SpriteBatch batch) {
        if (!collected) {
            batch.draw(regGoldCoin.texture, position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, regGoldCoin.regionX, regGoldCoin.regionY, regGoldCoin.regionWidth, regGoldCoin.regionHeight, false, false)
        }
    }

    int getScore() {
        return 100;
    }
}
