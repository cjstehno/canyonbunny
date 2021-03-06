package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Array
import com.packtpub.libgdx.canyonbunny.game.objects.*
import groovy.transform.TypeChecked

/**
 * FIXME: document me
 */
@TypeChecked
class Level {
    static final String TAG = Level.name

    enum BLOCK_TYPE {
        EMPTY(0, 0, 0), // black
        ROCK(0, 255, 0), // green
        PLAYER_SPAWNPOINT(255, 255, 255), // white
        ITEM_FEATHER(255, 0, 255), // purple
        ITEM_GOLD_COIN(255, 255, 0), // yellow
        GOAL(255, 0, 0)

        final int color

        private BLOCK_TYPE(int r, int g, int b) {
            color = r << 24 | g << 16 | b << 8 | 0xff
        }

        boolean sameColor(int color) {
            return this.color == color
        }
    }

    // objects
    Array<Rock> rocks

    // decoration
    Clouds clouds;
    Mountains mountains;
    WaterOverlay waterOverlay;

    BunnyHead bunnyHead
    Array<GoldCoin> goldCoins
    Array<Feather> feathers
    Array<Carrot> carrots
    Goal goal

    public Level(String filename) {
        init(filename)
    }

    private void init(String filename) {
        // player
        bunnyHead = null

        // objects
        rocks = new Array<Rock>()
        goldCoins = new Array<GoldCoin>()
        feathers = new Array<Feather>()
        carrots = new Array<>()

        // load image file that represents the level data
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename))

        // scan pixels from top-left to bottom-right
        int lastPixel = -1
        for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++) {
            for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++) {
                // height grows from bottom to top
                float baseHeight = pixmap.getHeight() - pixelY

                // get color of current pixel as 32-bit RGBA value
                int currentPixel = pixmap.getPixel(pixelX, pixelY)

                // find matching color value to identify block type at (x,y)
                // point and create the corresponding game object if there is
                // a match

                // empty space
                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
                    // do nothing
                }
                // rock
                else if (BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
                    if (lastPixel != currentPixel) {
                        AbstractGameObject obj = new Rock()
                        float heightIncreaseFactor = 0.25f
                        float offsetHeight = -2.5f
                        obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight as float)
                        rocks.add((Rock) obj)
                    } else {
                        rocks.get(rocks.size - 1).increaseLength(1)
                    }
                }
                // player spawn point
                else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
                    AbstractGameObject obj = new BunnyHead()
                    obj.position.set(pixelX, (baseHeight * obj.dimension.y - 3.0f) as float)
                    bunnyHead = (BunnyHead) obj
                }
                // feather
                else if (BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {
                    AbstractGameObject obj = new Feather()
                    obj.position.set(pixelX, (baseHeight * obj.dimension.y - 1.5f) as float)
                    feathers.add((Feather) obj)
                }
                // gold coin
                else if (BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel)) {
                    AbstractGameObject obj = new GoldCoin();
                    obj.position.set(pixelX, (baseHeight * obj.dimension.y - 1.5f) as float)
                    goldCoins.add((GoldCoin) obj)
                }
                // goal
                else if (BLOCK_TYPE.GOAL.sameColor(currentPixel)) {
                    Goal obj = new Goal()
                    obj.position.set(pixelX, (baseHeight - 7.0f) as float)
                    goal = (Goal) obj
                }
                // unknown object/pixel color
                else {
                    int r = 0xff & (currentPixel >>> 24)
                    //red color channel
                    int g = 0xff & (currentPixel >>> 16)
                    //green color channel
                    int b = 0xff & (currentPixel >>> 8)
                    //blue color channel
                    int a = 0xff & currentPixel
                    //alpha channel

                    Gdx.app.error TAG, "Unknown object at x<$pixelX> y<$pixelY>: r<$r> g<$g> b<$b> a<$a>"
                }
                lastPixel = currentPixel;
            }
        }

        // decoration
        clouds = new Clouds(pixmap.width)
        clouds.position.set(0, 2)
        mountains = new Mountains(pixmap.width)
        mountains.position.set(-1, -1)
        waterOverlay = new WaterOverlay(pixmap.width)
        waterOverlay.position.set(0, -3.75f)

        // free memory
        pixmap.dispose()
        Gdx.app.debug TAG, "level '$filename' loaded"
    }

    void render(SpriteBatch batch) {
        // Draw Mountains
        mountains.render(batch)

        // draw goal
        goal.render(batch)

        // Draw Rocks
        rocks.each { it.render(batch) }

        goldCoins.each { it.render(batch) }
        feathers.each { it.render(batch) }
        carrots.each { it.render(batch) }

        bunnyHead.render(batch)

        // Draw Water Overlay
        waterOverlay.render(batch)

        // Draw Clouds
        clouds.render(batch)
    }

    public void update(float deltaTime) {
        bunnyHead.update(deltaTime)

        rocks.each { it.update(deltaTime) }
        goldCoins.each { it.update(deltaTime) }
        feathers.each { it.update(deltaTime) }
        carrots.each { it.update(deltaTime) }

        clouds.update(deltaTime)
    }
}
