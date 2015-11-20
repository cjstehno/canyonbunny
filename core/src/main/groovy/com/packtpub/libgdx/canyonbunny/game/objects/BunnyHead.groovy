package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.packtpub.libgdx.canyonbunny.game.Assets
import com.packtpub.libgdx.canyonbunny.util.AudioManager
import com.packtpub.libgdx.canyonbunny.util.CharacterSkin
import com.packtpub.libgdx.canyonbunny.util.Constants
import com.packtpub.libgdx.canyonbunny.util.GamePreferences
import groovy.transform.TypeChecked

@TypeChecked
class BunnyHead extends AbstractGameObject {

    static final String TAG = BunnyHead.name

    enum VIEW_DIRECTION {
        LEFT, RIGHT
    }

    enum JUMP_STATE {
        GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
    }

    VIEW_DIRECTION viewDirection
    float timeJumping
    JUMP_STATE jumpState
    boolean hasFeatherPowerup
    float timeLeftFeatherPowerup

    ParticleEffect dustParticles = new ParticleEffect()

    private final float JUMP_TIME_MAX = 0.3f
    private final float JUMP_TIME_MIN = 0.1f
    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f

    private TextureRegion regHead

    BunnyHead() {
        init()
    }

    void init() {
        dimension.set(1, 1)

        regHead = Assets.instance.bunny.head;

        // Center image on game object
        origin.set((dimension.x / 2) as float, (dimension.y / 2) as float)

        // Bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y)

        // Set physics values
        terminalVelocity.set(3.0f, 4.0f)
        friction.set(12.0f, 0.0f)
        acceleration.set(0.0f, -25.0f)

        // View direction
        viewDirection = VIEW_DIRECTION.RIGHT

        // Jump state
        jumpState = JUMP_STATE.FALLING
        timeJumping = 0

        // Power-ups
        hasFeatherPowerup = false
        timeLeftFeatherPowerup = 0

        dustParticles.load(Gdx.files.classpath('particles/dust.pfx'), Gdx.files.classpath('particles'))
    }

    void setJumping(boolean jumpKeyPressed) {
        switch (jumpState) {
            case JUMP_STATE.GROUNDED: // Character is standing on a platform
                if (jumpKeyPressed) {
                    AudioManager.instance.play(Assets.instance.sounds.jump)

                    // Start counting jump time from the beginning
                    timeJumping = 0
                    jumpState = JUMP_STATE.JUMP_RISING
                }
                break

            case JUMP_STATE.JUMP_RISING: // Rising in the air
                if (!jumpKeyPressed)
                    jumpState = JUMP_STATE.JUMP_FALLING
                break

            case JUMP_STATE.FALLING: // Falling down
            case JUMP_STATE.JUMP_FALLING: // Falling down after jump
                if (jumpKeyPressed && hasFeatherPowerup()) {
                    AudioManager.instance.play(Assets.instance.sounds.jumpWithFeather, 1, MathUtils.random(1.0f, 1.1f))
                    timeJumping = JUMP_TIME_OFFSET_FLYING
                    jumpState = JUMP_STATE.JUMP_RISING
                }
                break
        }
    }

    void setFeatherPowerup(boolean pickedUp) {
        hasFeatherPowerup = pickedUp
        if (pickedUp) {
            timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION
        }
    }

    boolean hasFeatherPowerup() {
        return hasFeatherPowerup && timeLeftFeatherPowerup > 0
    }

    @Override
    public void render(SpriteBatch batch) {
        dustParticles.draw(batch)

        batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].color)

        // Set special color when game object has a feather power-up
        if (hasFeatherPowerup) {
            batch.setColor(1.0f, 0.8f, 0.0f, 1.0f)
        }

        // Draw image
        batch.draw(regHead.texture, position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, regHead.regionX, regHead.regionY, regHead.regionWidth, regHead.regionHeight, viewDirection == VIEW_DIRECTION.LEFT, false)

        // Reset color to white
        batch.setColor(1, 1, 1, 1)
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime)

        if (velocity.x != 0) {
            viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT
        }

        if (timeLeftFeatherPowerup > 0) {
            timeLeftFeatherPowerup -= deltaTime
            if (timeLeftFeatherPowerup < 0) {
                // disable power-up
                timeLeftFeatherPowerup = 0
                setFeatherPowerup(false)
            }
        }

        dustParticles.update(deltaTime)
    }

    @Override
    protected void updateMotionY(float deltaTime) {
        switch (jumpState) {
            case JUMP_STATE.GROUNDED:
                jumpState = JUMP_STATE.FALLING
                break

            case JUMP_STATE.JUMP_RISING:
                // Keep track of jump time
                timeJumping += deltaTime

                // Jump time left?
                if (timeJumping <= JUMP_TIME_MAX) {
                    // Still jumping
                    velocity.y = terminalVelocity.y
                }
                break

            case JUMP_STATE.FALLING:
                break

            case JUMP_STATE.JUMP_FALLING:
                // Add delta times to track jump time
                timeJumping += deltaTime

                // Jump to minimal height if jump key was pressed too short
                if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) {
                    // Still jumping
                    velocity.y = terminalVelocity.y
                }
        }

        if (jumpState != JUMP_STATE.GROUNDED) {
            dustParticles.allowCompletion()
            super.updateMotionY(deltaTime)
        }
    }
}
