package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.packtpub.libgdx.canyonbunny.game.Assets

class GoldCoin extends AbstractGameObject {

    boolean collected

    private TextureRegion regGoldCoin

    GoldCoin() {
        init()
    }

    private void init() {
        dimension.set(0.5f, 0.5f)

        setAnimation(Assets.instance.goldCoin.animGoldCoin)
        stateTime = MathUtils.random(0f, 1f)

        bounds.set(0f, 0f, dimension.x, dimension.y)

        collected = false
    }

    @Override
    public void render(SpriteBatch batch) {
        if (collected) return

        TextureRegion reg = animation.getKeyFrame(stateTime, true)
        batch.draw(reg.texture, position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.regionX, reg.regionY, reg.regionWidth, reg.regionHeight, false, false)
    }

    int getScore() {
        return 100;
    }
}
