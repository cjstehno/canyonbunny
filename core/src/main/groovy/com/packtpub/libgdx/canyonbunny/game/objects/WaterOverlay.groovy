package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.packtpub.libgdx.canyonbunny.game.Assets
import groovy.transform.TypeChecked

@TypeChecked
class WaterOverlay extends AbstractGameObject {

    private TextureRegion regWaterOverlay
    private float length

    WaterOverlay(float length) {
        this.length = length
        init()
    }

    private void init() {
        dimension.set(length * 10, 3)

        regWaterOverlay = Assets.instance.levelDecoration.waterOverlay

        origin.x = -dimension.x / 2
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion reg = regWaterOverlay
        batch.draw(reg.texture, position.x + origin.x, position.y + origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.regionX, reg.regionY, reg.regionWidth, reg.regionHeight, false, false)
    }
}
