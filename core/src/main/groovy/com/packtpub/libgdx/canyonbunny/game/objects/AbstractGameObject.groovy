package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import groovy.transform.TypeChecked

@TypeChecked
abstract class AbstractGameObject {

    Vector2 position = new Vector2()
    Vector2 dimension = new Vector2(1, 1)
    Vector2 origin = new Vector2()
    Vector2 scale = new Vector2(1, 1)
    float rotation = 0

    public void update(float deltaTime) {
    }

    public abstract void render(SpriteBatch batch)
}

