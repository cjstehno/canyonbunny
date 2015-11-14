package com.packtpub.libgdx.canyonbunny.util

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.packtpub.libgdx.canyonbunny.game.objects.AbstractGameObject
import groovy.transform.TypeChecked

@TypeChecked
class CameraHelper {

    private static final String TAG = CameraHelper.name

    private final float MAX_ZOOM_IN = 0.25f
    private final float MAX_ZOOM_OUT = 10.0f

    AbstractGameObject target

    private Vector2 position = new Vector2()
    private float zoom = 1.0f

    public void update(float deltaTime) {
        if (!hasTarget()) return

        position.x = target.position.x + target.origin.x
        position.y = target.position.y + target.origin.y

        // prevent camera from going down too far
        position.y = Math.max(-1f, position.y)
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y)
    }

    public Vector2 getPosition() { return position }

    public void addZoom(float amount) {
        setZoom((zoom + amount) as float)
    }

    public void setZoom(float zoom) {
        this.zoom = MathUtils.clamp(zoom, MAX_ZOOM_IN, MAX_ZOOM_OUT)
    }

    public float getZoom() { return zoom }

    public boolean hasTarget() { return target != null }

    public boolean hasTarget(AbstractGameObject target) {
        return hasTarget() && this.target.equals(target)
    }

    public void applyTo(OrthographicCamera camera) {
        camera.position.x = position.x
        camera.position.y = position.y
        camera.zoom = zoom
        camera.update()
    }
}
