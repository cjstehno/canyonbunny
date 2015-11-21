package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.packtpub.libgdx.canyonbunny.game.Assets
import groovy.transform.TypeChecked

@TypeChecked
class Rock extends AbstractGameObject {

    private TextureRegion regEdge
    private TextureRegion regMiddle
    private int length

    private final float FLOAT_CYCLE_TIME = 2.0f
    private final float FLOAT_AMPLITUDE = 0.25f
    private float floatCycleTimeLeft
    private boolean floatingDownwards
    private Vector2 floatTargetPosition

    Rock() {
        init()
    }

    private void init() {
        dimension.set(1, 1.5f)

        regEdge = Assets.instance.rock.edge
        regMiddle = Assets.instance.rock.middle

        setLength(1)

        floatingDownwards = false
        floatCycleTimeLeft = MathUtils.random(0f, (FLOAT_CYCLE_TIME / 2) as float)
        floatTargetPosition = null
    }

    void setLength(int len) {
        this.length = len

        bounds.set(0f, 0f, (dimension.x * length) as float, dimension.y)
    }

    void increaseLength(int amount) {
        setLength(length + amount)
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime)

        floatCycleTimeLeft -= deltaTime
        if (floatCycleTimeLeft <= 0) {
            floatCycleTimeLeft = FLOAT_CYCLE_TIME
            floatingDownwards = !floatingDownwards
            body.setLinearVelocity(0f, (FLOAT_AMPLITUDE * (floatingDownwards ? -1 : 1)) as float)
        } else {
            body.setLinearVelocity(body.getLinearVelocity().scl(0.98f))

        }
    }

    @Override
    void render(SpriteBatch batch) {
        float relX = 0f
        float relY = 0f

        // draw left edge
        TextureRegion reg = regEdge
        relX -= (dimension.x / 4) as float
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
