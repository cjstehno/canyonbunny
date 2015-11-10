package com.packtpub.libgdx.canyonbunny.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetErrorListener
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
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

    private AssetManager assetManager

    void init(AssetManager assetManager) {
        this.assetManager = assetManager
        assetManager.errorListener = this
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas)
        assetManager.finishLoading()

        Gdx.app.debug TAG, "# of assets loaded: ${assetManager.assetNames.size}"
        assetManager.assetNames.each { a ->
            Gdx.app.debug TAG, "asset: $a"
        }

        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS)

        // enabled texture filtering for pixel smoothing
        atlas.textures*.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)

        // create game resource objects
        bunny = new AssetBunny(atlas)
        rock = new AssetRock(atlas)
        goldCoin = new AssetGoldCoin(atlas)
        feather = new AssetFeather(atlas)
        levelDecoration = new AssetLevelDecoration(atlas)
    }

    @Override
    void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error TAG, "Couldn't load asset: ${asset.fileName}", throwable as Exception
    }

    @Override
    void dispose() {
        assetManager.dispose()
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
}
