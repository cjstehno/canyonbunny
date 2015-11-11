package com.packtpub.libgdx.canyonbunny.desktop
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.badlogic.gdx.tools.texturepacker.TexturePacker
import com.packtpub.libgdx.canyonbunny.CanyonBunnyMain


public class DesktopLauncher {

    private static boolean rebuildAtlas = false
    private static drawDebugOutline = true

    static void main(arg) {
        if( rebuildAtlas ){
            TexturePacker.Settings settings = new TexturePacker.Settings()
            settings.maxWidth = 1024
            settings.maxHeight = 1024
            settings.duplicatePadding = false
            settings.debug = drawDebugOutline

            TexturePacker.process(settings, '../raw-assets/images', '../src/resources/assets/images' , 'canyonbunny.pack')
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
        config.width = 800
        config.height = 480
        new LwjglApplication(new CanyonBunnyMain(), config)
    }
}
