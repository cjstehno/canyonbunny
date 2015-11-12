package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.packtpub.libgdx.canyonbunny.game.Assets

/**
 * FIXME: document me
 */
class Rock extends AbstractGameObject {

    private TextureRegion regEdge
    private TextureRegion regMiddle
    private int length

    Rock() {
        init()
    }

    private void init() {
        dimension.set(1, 1.5f)

        regEdge = Assets.instance.rock.edge
        regMiddle = Assets.instance.rock.middle

        setLength(1)
    }

    void setLength(int len) {
        this.length = len
    }

    void increaseLength(int amount) {
        setLength(length + amount)
    }

    @Override
    void render(SpriteBatch batch) {
        float relX = 0f
        float relY = 0f

        // draw left edge
        TextureRegion reg = regEdge
        relX -= dimension.x / 4
        batch.draw(
            reg.texture,
            (position.x + relX) as float,
            (position.y + relY) as float,
            origin.x,
            origin.y,
            (dimension.x / 4) as float,
            dimension.y,
            scale.x,
            scale.y,
            rotation,
            reg.regionX,
            reg.regionY,
            reg.regionWidth,
            reg.regionHeight,
            false,
            false
        )

        // Draw middle
        relX = 0
        reg = regMiddle
        for (int i = 0; i < length; i++) {
            batch.draw(
                reg.texture,
                (position.x + relX) as float,
                (position.y + relY) as float,
                origin.x,
                origin.y,
                dimension.x,
                dimension.y,
                scale.x,
                scale.y,
                rotation,
                reg.regionX,
                reg.regionY,
                reg.regionWidth,
                reg.regionHeight,
                false,
                false
            )
            relX += dimension.x
        }

        // Draw right edge
        reg = regEdge
        batch.draw(
            reg.texture,
            (position.x + relX) as float,
            (position.y + relY) as float,
            (origin.x + dimension.x / 8) as float,
            origin.y,
            (dimension.x / 4) as float,
            dimension.y,
            scale.x,
            scale.y,
            rotation,
            reg.regionX,
            reg.regionY,
            reg.regionWidth,
            reg.regionHeight,
            true,
            false
        )
    }
}
