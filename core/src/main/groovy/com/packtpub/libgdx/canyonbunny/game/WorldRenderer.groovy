package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.packtpub.libgdx.canyonbunny.util.Constants
import groovy.transform.TypeChecked

@TypeChecked
class WorldRenderer implements Disposable {

    private OrthographicCamera camera
    private SpriteBatch batch
    private WorldController worldController

    WorldRenderer(WorldController worldController) {
        this.worldController = worldController
        init()
    }

    private void init() {
        batch = new SpriteBatch()
        camera = new OrthographicCamera(Constants.VIEWPOINT_WIDTH, Constants.VIEWPOINT_HEIGHT)
        camera.position.set(0, 0, 0)
        camera.update()
    }

    void render() {
        renderTestObjects()
    }

    private void renderTestObjects() {
        worldController.cameraHelper.applyTo(camera)

        batch.projectionMatrix = camera.combined

        batch.begin()

        for (Sprite sprite : worldController.testSprites) {
            sprite.draw(batch)
        }

        batch.end()
    }

    void resize(int width, int height) {
        camera.viewportWidth = ((Constants.VIEWPOINT_HEIGHT / height) * width) as float
        camera.update()
    }

    @Override
    void dispose() {
        batch.dispose()
    }
}
