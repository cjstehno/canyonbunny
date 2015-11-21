package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion
import com.badlogic.gdx.utils.Disposable
import com.packtpub.libgdx.canyonbunny.util.Constants

@Singleton
class Assets implements Disposable, AssetErrorListener {

    static final String TAG = Assets.name

    AssetBunny bunny
    AssetRock rock
    AssetGoldCoin goldCoin
    AssetFeather feather
    AssetLevelDecoration levelDecoration
    AssetFonts fonts
    AssetSounds sounds
    AssetMusic music

    private AssetManager assetManager

    void init(AssetManager assetManager) {
        this.assetManager = assetManager
        assetManager.errorListener = this
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas)

        // load sounds
        assetManager.load('sounds/jump.wav', Sound)
        assetManager.load('sounds/jump_with_feather.wav', Sound)
        assetManager.load('sounds/pickup_coin.wav', Sound)
        assetManager.load('sounds/pickup_feather.wav', Sound)
        assetManager.load('sounds/live_lost.wav', Sound)

        // load music
        assetManager.load('music/keith303_-_brand_new_highscore.mp3', Music)

        assetManager.finishLoading()

        Gdx.app.debug TAG, "# of assets loaded: ${assetManager.assetNames.size}"
        assetManager.assetNames.each { a ->
            Gdx.app.debug TAG, "asset: $a"
        }

        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS)

        // enabled texture filtering for pixel smoothing
        atlas.textures*.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)

        // create game resource objects
        fonts = new AssetFonts()
        bunny = new AssetBunny(atlas)
        rock = new AssetRock(atlas)
        goldCoin = new AssetGoldCoin(atlas)
        feather = new AssetFeather(atlas)
        levelDecoration = new AssetLevelDecoration(atlas)
        sounds = new AssetSounds(assetManager)
        music = new AssetMusic(assetManager)
    }

    @Override
    void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error TAG, "Couldn't load asset: ${asset.fileName}", throwable as Exception
    }

    @Override
    void dispose() {
        assetManager.dispose()
        fonts.defaultSmall.dispose()
        fonts.defaultNormal.dispose()
        fonts.defaultBig.dispose()
    }

    class AssetBunny {

        final AtlasRegion head

        AssetBunny(TextureAtlas atlas) {
            head = atlas.findRegion('bunny_head')
        }
    }

    class AssetRock {

        final AtlasRegion edge
        final AtlasRegion middle

        AssetRock(TextureAtlas atlas) {
            edge = atlas.findRegion('rock_edge')
            middle = atlas.findRegion('rock_middle')
        }
    }

    class AssetGoldCoin {

        final AtlasRegion goldCoin

        AssetGoldCoin(TextureAtlas atlas) {
            goldCoin = atlas.findRegion('item_gold_coin')
        }
    }

    class AssetFeather {

        final AtlasRegion feather

        AssetFeather(TextureAtlas atlas) {
            feather = atlas.findRegion('item_feather')
        }
    }

    class AssetLevelDecoration {

        final AtlasRegion cloud01
        final AtlasRegion cloud02
        final AtlasRegion cloud03
        final AtlasRegion mountainLeft
        final AtlasRegion mountainRight
        final AtlasRegion waterOverlay

        AssetLevelDecoration(TextureAtlas atlas) {
            cloud01 = atlas.findRegion('cloud01')
            cloud02 = atlas.findRegion('cloud02')
            cloud03 = atlas.findRegion('cloud03')
            mountainLeft = atlas.findRegion('mountain_left')
            mountainRight = atlas.findRegion('mountain_right')
            waterOverlay = atlas.findRegion('water_overlay')
        }
    }

    class AssetFonts {
        final BitmapFont defaultSmall
        final BitmapFont defaultNormal
        final BitmapFont defaultBig

        AssetFonts() {
            defaultSmall = new BitmapFont(Gdx.files.classpath('fonts/arial-15.fnt'), true)
            defaultNormal = new BitmapFont(Gdx.files.classpath('fonts/arial-15.fnt'), true)
            defaultBig = new BitmapFont(Gdx.files.classpath('fonts/arial-15.fnt'), true)

            defaultSmall.data.setScale(0.75f)
            defaultNormal.data.setScale(1.0f)
            defaultBig.data.setScale(2.0f)

            defaultSmall.region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            defaultNormal.region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            defaultBig.region.texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        }
    }

    class AssetSounds {
        final Sound jump
        final Sound jumpWithFeather
        final Sound pickupCoin
        final Sound pickupFeather
        final Sound liveLost

        AssetSounds(AssetManager am) {
            jump = am.get('sounds/jump.wav', Sound)
            jumpWithFeather = am.get('sounds/jump_with_feather.wav', Sound)
            pickupCoin = am.get('sounds/pickup_coin.wav', Sound)
            pickupFeather = am.get('sounds/pickup_feather.wav', Sound)
            liveLost = am.get('sounds/live_lost.wav', Sound)
        }
    }

    class AssetMusic {
        final Music song01

        AssetMusic(AssetManager am) {
            song01 = am.get('music/keith303_-_brand_new_highscore.mp3', Music)
        }
    }
}
