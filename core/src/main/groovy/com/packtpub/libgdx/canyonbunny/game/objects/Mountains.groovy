package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.packtpub.libgdx.canyonbunny.game.Assets
import groovy.transform.TypeChecked

@TypeChecked
class Mountains extends AbstractGameObject {

    private TextureRegion regMountainLeft
    private TextureRegion regMountainRight
    private int length

    Mountains(int length) {
        this.length = length
        init()
    }

    private void init() {
        dimension.set(10, 2)

        regMountainLeft = Assets.instance.levelDecoration.mountainLeft
        regMountainRight = Assets.instance.levelDecoration.mountainRight

        origin.x = (-dimension.x * 2) as float
        length += (dimension.x * 2) as int
    }

    public void updateScrollPosition(Vector2 camPosition) {
        position.set(camPosition.x, position.y);
    }

    private void drawMountain(SpriteBatch batch, float offsetX, float offsetY, float tintColor, float parallaxSpeedX) {
        batch.setColor(tintColor, tintColor, tintColor, 1)

        float xRel = dimension.x * offsetX
        float yRel = dimension.y * offsetY

        // mountains span the whole level
        int mountainLength = 0
        mountainLength += MathUtils.ceil((length / (2 * dimension.x) * (1 - parallaxSpeedX)) as float)
        mountainLength += MathUtils.ceil(0.5f + offsetX as float)

        for (int i = 0; i < mountainLength; i++) {
            // mountain left
            TextureRegion reg = regMountainLeft
            batch.draw(reg.texture, (origin.x + xRel + position.x * parallaxSpeedX) as float, (origin.y + yRel + position.y) as float, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.regionX, reg.regionY, reg.regionWidth, reg.regionHeight, false, false)

            xRel += dimension.x

            // mountain right
            reg = regMountainRight
            batch.draw(reg.texture, (origin.x + xRel + position.x * parallaxSpeedX) as float, (origin.y + yRel + position.y) as float, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.regionX, reg.regionY, reg.regionWidth, reg.regionHeight, false, false)
            xRel += dimension.x
        }

        // reset color to white
        batch.setColor(1, 1, 1, 1)
    }

    @Override
    public void render(SpriteBatch batch) {
        // 80% distant mountains (dark gray)
        drawMountain(batch, 0.5f, 0.5f, 0.5f, 0.8f)

        // 50% distant mountains (gray)
        drawMountain(batch, 0.25f, 0.25f, 0.7f, 0.5f)

        // 30% distant mountains (light gray)
        drawMountain(batch, 0.0f, 0.0f, 0.9f, 0.3f)
    }
}
