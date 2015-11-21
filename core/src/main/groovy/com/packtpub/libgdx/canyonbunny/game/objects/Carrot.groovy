package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.packtpub.libgdx.canyonbunny.game.Assets
import groovy.transform.TypeChecked

@TypeChecked
class Carrot extends AbstractGameObject {

    private TextureRegion regCarrot

    Carrot() {
        init()
    }

    private void init() {
        dimension.set(0.25f, 0.5f)

        regCarrot = Assets.instance.levelDecoration.carrot

        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y)
        origin.set((dimension.x / 2) as float, (dimension.y / 2) as float)
    }

    public void render(SpriteBatch batch) {
        batch.draw(regCarrot.texture, (position.x - origin.x) as float, (position.y - origin.y) as float, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, regCarrot.regionX, regCarrot.regionY, regCarrot.regionWidth, regCarrot.regionHeight, false, false)
    }
}
