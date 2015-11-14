package com.packtpub.libgdx.canyonbunny.util

import groovy.transform.TypeChecked

@TypeChecked
class Constants {

    // Visible game world is 5 meters wide
    static final float VIEWPOINT_WIDTH = 5.0f

    // Visible game world if 5 meter tall
    static final float VIEWPOINT_HEIGHT = 5.0f

    // GUI width
    static final float VIEWPORT_GUI_WIDTH = 800.0f

    // GUI height
    static final float VIEWPORT_GUI_HEIGHT = 480.0f

    static final String TEXTURE_ATLAS_OBJECTS = 'images/canyonbunny.pack.atlas'

    static final String LEVEL_01 = 'levels/level-01.png'

    static final int LIVES_START = 3

    static final float ITEM_FEATHER_POWERUP_DURATION = 9

    static final float TIME_DELAY_GAME_OVER = 3
}
