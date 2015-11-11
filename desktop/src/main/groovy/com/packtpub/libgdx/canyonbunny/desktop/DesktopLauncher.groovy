package com.packtpub.libgdx.canyonbunny.desktop
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.packtpub.libgdx.canyonbunny.CanyonBunnyMain

public class DesktopLauncher {

    static void main(arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
        config.width = 800
        config.height = 480
        new LwjglApplication(new CanyonBunnyMain(), config)
    }
}
