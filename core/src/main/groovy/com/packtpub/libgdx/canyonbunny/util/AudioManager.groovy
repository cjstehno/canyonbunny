package com.packtpub.libgdx.canyonbunny.util

import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import groovy.transform.TypeChecked

@TypeChecked @Singleton
class AudioManager {

    private Music playingMusic

    void play(Sound sound, float volume = 1f, float pitch = 1f, float pan = 0) {
        if (!GamePreferences.instance.sound) return
        sound.play((GamePreferences.instance.volSound * volume) as float, pitch, pan)
    }

    void play(Music music) {
        stopMusic()
        playingMusic = music
        if (GamePreferences.instance.music) {
            music.looping = true
            music.volume = GamePreferences.instance.volMusic
            music.play()
        }
    }

    void stopMusic() {
        if (playingMusic) playingMusic.stop()
    }

    void onSettingsUpdated() {
        if (!playingMusic) return

        playingMusic.volume = GamePreferences.instance.volMusic

        if (GamePreferences.instance.music) {
            if (!playingMusic.isPlaying()) playingMusic.play()
        } else {
            playingMusic.pause()
        }
    }
}
