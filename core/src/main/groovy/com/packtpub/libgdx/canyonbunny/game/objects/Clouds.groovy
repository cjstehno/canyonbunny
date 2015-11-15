package com.packtpub.libgdx.canyonbunny.game.objects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.canyonbunny.game.Assets
import groovy.transform.Canonical
import groovy.transform.TypeChecked

@TypeChecked
class Clouds extends AbstractGameObject {

    private Array<TextureRegion> regClouds
    private Array<Cloud> clouds
    private float length

    Clouds(float length) {
        this.length = length
        init()
    }

    private void init() {
        dimension.set(3.0f, 1.5f)

        regClouds = new Array<TextureRegion>()
        regClouds.add(Assets.instance.levelDecoration.cloud01)
        regClouds.add(Assets.instance.levelDecoration.cloud02)
        regClouds.add(Assets.instance.levelDecoration.cloud03)

        int distFac = 5
        int numClouds = (int) (length / distFac)

        clouds = new Array<Cloud>(2 * numClouds)

        for (int i = 0; i < numClouds; i++) {
            Cloud cloud = spawnCloud()
            cloud.position.x = i * distFac
            clouds.add(cloud)
        }
    }

    private Cloud spawnCloud() {
        Cloud cloud = new Cloud()
        cloud.dimension.set(dimension)

        // select random cloud image
        cloud.setRegion(regClouds.random())

        // position
        Vector2 pos = new Vector2()
        pos.x = length + 10 // position after end of level
        pos.y += 1.75 // base position
        pos.y += MathUtils.random(0.0f, 0.2f) * (MathUtils.randomBoolean() ? 1 : -1) // random additional position
        cloud.position.set(pos)

        Vector2 speed = new Vector2()
        speed.x += 0.5f
        speed.x += MathUtils.random(0f, 0.75f)

        cloud.terminalVelocity.set(speed)
        speed.x *= -1
        cloud.velocity.set(speed)

        return cloud
    }

    @Override
    public void update(float deltaTime) {
        for (int i = clouds.size - 1; i >= 0; i--) {
            Cloud cloud = clouds.get(i)
            cloud.update(deltaTime)

            if (cloud.position.x < -10) {
                // cloud moved outside of world. destroy and spawn new cloud at end of level.
                clouds.removeIndex(i)
                clouds.add(spawnCloud())
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        clouds.each { it.render(batch) }
    }

    @Canonical
    private class Cloud extends AbstractGameObject {

        TextureRegion region

        @Override
        public void render(SpriteBatch batch) {
            batch.draw(region.texture, (position.x + origin.x) as float, (position.y + origin.y) as float, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, region.regionX, region.regionY, region.regionWidth, region.regionHeight, false, false)
        }
    }
}
