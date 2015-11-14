package com.packtpub.libgdx.canyonbunny.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import groovy.transform.TypeChecked

import static com.badlogic.gdx.math.MathUtils.clamp

@Singleton @TypeChecked
class GamePreferences {

    static final String TAG = GamePreferences.name

    boolean sound
    boolean music
    float volSound
    float volMusic
    int charSkin
    boolean showFpsCounter

    private Preferences prefs = Gdx.app.getPreferences(Constants.PREFERENCES)

    void load() {
        sound = prefs.getBoolean('sound', true)
        music = prefs.getBoolean('music', true)
        volSound = clamp(prefs.getFloat('volSound', 0.5f), 0.0f, 1.0f)
        volMusic = clamp(prefs.getFloat('volMusic', 0.5f), 0.0f, 1.0f)
        charSkin = clamp(prefs.getInteger('charSkin', 0), 0, 2)
        showFpsCounter = prefs.getBoolean('showFpsCounter', false)
    }

    void save() {
        prefs.putBoolean('sound', sound)
        prefs.putBoolean('music', music)
        prefs.putFloat('volSound', volSound)
        prefs.putFloat('volMusic', volMusic)
        prefs.putInteger('charSkin', charSkin)
        prefs.putBoolean('showFpsCounter', showFpsCounter)
        prefs.flush()
    }
}
