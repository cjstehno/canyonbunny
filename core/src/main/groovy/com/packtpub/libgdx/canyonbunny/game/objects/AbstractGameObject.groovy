package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import groovy.transform.TypeChecked

@TypeChecked
abstract class AbstractGameObject {

    Vector2 position = new Vector2()
    Vector2 dimension = new Vector2(1, 1)
    Vector2 origin = new Vector2()
    Vector2 scale = new Vector2(1, 1)

    Vector2 velocity = new Vector2()
    Vector2 terminalVelocity = new Vector2(1, 1)
    Vector2 friction = new Vector2()
    Vector2 acceleration = new Vector2()
    Rectangle bounds = new Rectangle()

    Body body

    float stateTime
    private Animation animation

    float rotation = 0

    void setAnimation(Animation animation) {
        this.animation = animation
        stateTime = 0
    }

    Animation getAnimation(){
        return animation
    }

    public void update(float deltaTime) {
        stateTime += deltaTime

        if (!body) {
            updateMotionX(deltaTime)
            updateMotionY(deltaTime)

            // Move to new position
            position.x += velocity.x * deltaTime
            position.y += velocity.y * deltaTime

        } else {
            position.set(body.position)
            rotation = (body.angle * MathUtils.radiansToDegrees) as float
        }
    }

    public abstract void render(SpriteBatch batch)

    protected void updateMotionX(float deltaTime) {
        if (velocity.x != 0) {
            // Apply friction
            if (velocity.x > 0) {
                velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0)
            } else {
                velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0)
            }
        }

        // Apply acceleration
        velocity.x += acceleration.x * deltaTime

        // Make sure the object's velocity does not exceed the positive or negative terminal velocity
        velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x)
    }

    protected void updateMotionY(float deltaTime) {
        if (velocity.y != 0) {
            // Apply friction
            if (velocity.y > 0) {
                velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0)
            } else {
                velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0)
            }
        }

        // Apply accelerationvelocity.y += acceleration.y * deltaTime;
        // Make sure the object's velocity does not exceed the positive or negative terminal velocity
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y)
    }
}

