package com.packtpub.libgdx.canyonbunny.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.packtpub.libgdx.canyonbunny.game.Assets
import com.packtpub.libgdx.canyonbunny.screens.transitions.ScreenTransitionFade
import com.packtpub.libgdx.canyonbunny.util.AudioManager
import com.packtpub.libgdx.canyonbunny.util.CharacterSkin
import com.packtpub.libgdx.canyonbunny.util.GamePreferences
import groovy.transform.TypeChecked

import static com.badlogic.gdx.math.Interpolation.*
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import static com.packtpub.libgdx.canyonbunny.util.Constants.*
import static groovy.transform.TypeCheckingMode.SKIP

@TypeChecked
class MenuScreen extends AbstractGameScreen {

    private static final String TAG = MenuScreen.name

    private Stage stage
    private Skin skinCanyonBunny

    // menu
    private Image imgBackground
    private Image imgLogo
    private Image imgInfo
    private Image imgCoins
    private Image imgBunny
    private Button btnMenuPlay
    private Button btnMenuOptions

    // options
    private Window winOptions
    private TextButton btnWinOptSave
    private TextButton btnWinOptCancel
    private CheckBox chkSound
    private Slider sldSound
    private CheckBox chkMusic
    private Slider sldMusic
    private SelectBox<CharacterSkin> selCharSkin
    private Image imgCharSkin
    private CheckBox chkShowFpsCounter

    // debug
    private final float DEBUG_REBUILD_INTERVAL = 5.0f
    private boolean debugEnabled = false
    private float debugRebuildStage

    private Skin skinLibgdx

    MenuScreen(DirectedGame game) {
        super(game)
    }

    InputProcessor getInputProcessor() {
        return stage
    }

    @Override
    void render(float deltaTime) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        if (debugEnabled) {
            debugRebuildStage -= deltaTime
            if (debugRebuildStage <= 0) {
                debugRebuildStage = DEBUG_REBUILD_INTERVAL
                rebuildStage()
            }
        }
        stage.act(deltaTime)
        stage.draw()
        //        Table.drawDebug(stage) - TODO: this seems to have been moved
    }

    @Override
    void resize(int width, int height) {
        stage.getViewport().update(width, height, true)
    }

    @Override
    void hide() {
        stage.dispose()
        skinCanyonBunny.dispose()
        skinLibgdx.dispose()
    }

    @Override
    void show() {
        stage = new Stage(new StretchViewport(VIEWPORT_GUI_WIDTH, VIEWPORT_GUI_HEIGHT))
        rebuildStage()
    }

    @Override
    void pause() {

    }

    private void rebuildStage() {
        skinCanyonBunny = new Skin(Gdx.files.internal(SKIN_CANYONBUNNY_UI), new TextureAtlas(TEXTURE_ATLAS_UI))
        skinLibgdx = new Skin(Gdx.files.internal(SKIN_LIBGDX_UI), new TextureAtlas(TEXTURE_ATLAS_LIBGDX_UI))

        // build all layers
        Table layerBackground = buildBackgroundLayer()
        Table layerObjects = buildObjectsLayer()
        Table layerLogos = buildLogosLayer()
        Table layerControls = buildControlsLayer()
        Table layerOptionsWindow = buildOptionsWindowLayer()

        // assemble stage for menu screen
        stage.clear()
        Stack stack = new Stack()
        stage.addActor(stack)
        stack.setSize(VIEWPORT_GUI_WIDTH, VIEWPORT_GUI_HEIGHT)
        stack.add(layerBackground)
        stack.add(layerObjects)
        stack.add(layerLogos)
        stack.add(layerControls)
        stage.addActor(layerOptionsWindow)
    }

    private Table buildBackgroundLayer() {
        Table layer = new Table()
        imgBackground = new Image(skinCanyonBunny, "background")
        layer.add(imgBackground)
        return layer
    }

    private Table buildObjectsLayer() {
        Table layer = new Table()

        // + Coins
        imgCoins = new Image(skinCanyonBunny, 'coins')
        layer.addActor(imgCoins)
        imgCoins.setOrigin((imgCoins.width / 2) as float, (imgCoins.height / 2) as float)
        imgCoins.addAction(
            sequence(
                moveTo(135f, -20f),
                scaleTo(0f, 0f),
                fadeOut(0f),
                delay(2.5f),
                parallel(
                    moveBy(0f, 100f, 0.5f, swingOut),
                    scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear), alpha(1.0f, 0.5f)
                )
            )
        )

        // + Bunny
        imgBunny = new Image(skinCanyonBunny, 'bunny')
        layer.addActor(imgBunny)
        imgBunny.addAction(
            sequence(
                moveTo(655f, 510f),
                delay(4.0f),
                moveBy(-70f, -100f, 0.5f, fade),
                moveBy(-100f, -50f, 0.5f, fade),
                moveBy(-150f, -300f, 1.0f, elasticIn)
            )
        )

        return layer
    }

    private Table buildLogosLayer() {
        Table layer = new Table()
        layer.left().top()

        // + Game Logo
        imgLogo = new Image(skinCanyonBunny, "logo")
        layer.add(imgLogo)
        layer.row().expandY()

        // + Info Logos
        imgInfo = new Image(skinCanyonBunny, "info")
        layer.add(imgInfo).bottom()
        if (debugEnabled) layer.debug()

        return layer
    }

    private Table buildControlsLayer() {
        Table layer = new Table()
        layer.right().bottom()

        // + Play Button
        btnMenuPlay = new Button(skinCanyonBunny, "play")
        layer.add(btnMenuPlay)
        btnMenuPlay.addListener({ ChangeListener.ChangeEvent evt, Actor actor -> onPlayClicked() } as ChangeListener)
        layer.row()

        // + Options Button
        btnMenuOptions = new Button(skinCanyonBunny, "options");
        layer.add(btnMenuOptions);
        btnMenuOptions.addListener({ ChangeListener.ChangeEvent evt, Actor actor -> onOptionsClicked() } as ChangeListener)

        if (debugEnabled) layer.debug()

        return layer
    }

    private void showMenuButtons(boolean visible) {
        float moveDuration = 1.0f
        Interpolation moveEasing = swing
        float delayOptionsButton = 0.25f

        float moveX = (300 * (visible ? -1 : 1)) as float
        float moveY = (0 * (visible ? -1 : 1)) as float
        final Touchable touchEnabled = visible ? Touchable.enabled : Touchable.disabled
        btnMenuPlay.addAction(moveBy(moveX, moveY, moveDuration, moveEasing))

        btnMenuOptions.addAction(sequence(delay(delayOptionsButton), moveBy(moveX, moveY, moveDuration, moveEasing)))

        SequenceAction seq = sequence()
        if (visible)
            seq.addAction(delay((delayOptionsButton + moveDuration) as float))

        seq.addAction(run({
            btnMenuPlay.setTouchable(touchEnabled)
            btnMenuOptions.setTouchable(touchEnabled)
        } as Runnable))

        stage.addAction(seq)
    }

    private void showOptionsWindow(boolean visible, boolean animated) {
        winOptions.addAction(
            sequence(
                touchable(visible ? Touchable.enabled : Touchable.disabled),
                alpha((visible ? 0.8f : 0.0f) as float, (animated ? 1.0f : 0.0f) as float)
            )
        )
    }

    private void onPlayClicked() {
        game.setScreen(new GameScreen(game), ScreenTransitionFade.init(0.75f))
    }

    private void onOptionsClicked() {
        loadSettings()
        showMenuButtons(false)
        showOptionsWindow(true, true)
    }

    private void loadSettings() {
        GamePreferences prefs = GamePreferences.instance
        prefs.load()

        chkSound.setChecked(prefs.sound)
        sldSound.setValue(prefs.volSound)
        chkMusic.setChecked(prefs.music)
        sldMusic.setValue(prefs.volMusic)
        selCharSkin.setSelectedIndex(prefs.charSkin)
        onCharSkinSelected(prefs.charSkin)
        chkShowFpsCounter.setChecked(prefs.showFpsCounter)
    }

    private void saveSettings() {
        GamePreferences prefs = GamePreferences.instance
        prefs.sound = chkSound.isChecked()
        prefs.volSound = sldSound.getValue()
        prefs.music = chkMusic.isChecked()
        prefs.volMusic = sldMusic.getValue()
        prefs.charSkin = selCharSkin.getSelectedIndex()
        prefs.showFpsCounter = chkShowFpsCounter.isChecked()
        prefs.save()
    }

    private void onCharSkinSelected(int index) {
        imgCharSkin.color = (CharacterSkin.values()[index]).color
    }

    private void onSaveClicked() {
        saveSettings()
        onCancelClicked()
        AudioManager.instance.onSettingsUpdated()
    }

    private void onCancelClicked() {
        showMenuButtons(true)
        showOptionsWindow(false, true)
        AudioManager.instance.onSettingsUpdated()
    }

    private Table buildOptWinAudioSettings() {
        Table tbl = new Table()

        // + Title: "Audio"
        tbl.pad(10, 10, 0, 10)
        tbl.add(new Label("Audio", skinLibgdx, "default-font", Color.ORANGE)).colspan(3)
        tbl.row()
        tbl.columnDefaults(0).padRight(10)
        tbl.columnDefaults(1).padRight(10)

        // + Checkbox, "Sound" label, sound volume slider
        chkSound = new CheckBox("", skinLibgdx)
        tbl.add(chkSound)
        tbl.add(new Label("Sound", skinLibgdx))
        sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx)
        tbl.add(sldSound)
        tbl.row()

        // + Checkbox, "Music" label, music volume slider
        chkMusic = new CheckBox("", skinLibgdx)
        tbl.add(chkMusic)
        tbl.add(new Label("Music", skinLibgdx))
        sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx)
        tbl.add(sldMusic)
        tbl.row()

        return tbl
    }

    @TypeChecked(SKIP)
    private Table buildOptWinSkinSelection() {
        Table tbl = new Table()

        // + Title: "Character Skin"
        tbl.pad(10, 10, 0, 10)
        tbl.add(new Label("Character Skin", skinLibgdx, "default-font", Color.ORANGE)).colspan(2)
        tbl.row()

        // + Drop down box filled with skin items
        selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx)
        selCharSkin.setItems(*CharacterSkin.values())
        selCharSkin.addListener({ ChangeListener.ChangeEvent event, Actor actor ->
            onCharSkinSelected((actor as SelectBox<CharacterSkin>).selectedIndex)
        } as ChangeListener)
        tbl.add(selCharSkin).width(120).padRight(20)

        // + Skin preview image
        imgCharSkin = new Image(Assets.instance.bunny.head)
        tbl.add(imgCharSkin).width(50).height(50)

        return tbl
    }

    private Table buildOptWinDebug() {
        Table tbl = new Table()

        // + Title: "Debug"
        tbl.pad(10, 10, 0, 10)
        tbl.add(new Label("Debug", skinLibgdx, "default-font", Color.RED)).colspan(3)
        tbl.row()
        tbl.columnDefaults(0).padRight(10)
        tbl.columnDefaults(1).padRight(10)

        // + Checkbox, "Show FPS Counter" label
        chkShowFpsCounter = new CheckBox("", skinLibgdx)
        tbl.add(new Label("Show FPS Counter", skinLibgdx))
        tbl.add(chkShowFpsCounter)
        tbl.row()

        return tbl
    }

    private Table buildOptWinButtons() {
        Table tbl = new Table()

        // + Separator
        Label lbl = null
        lbl = new Label("", skinLibgdx)
        lbl.setColor(0.75f, 0.75f, 0.75f, 1)
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()))
        lbl.getStyle().background = skinLibgdx.newDrawable("white")
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1)
        tbl.row()

        lbl = new Label("", skinLibgdx)
        lbl.setColor(0.5f, 0.5f, 0.5f, 1)
        lbl.setStyle(new Label.LabelStyle(lbl.getStyle()))
        lbl.getStyle().background = skinLibgdx.newDrawable("white")
        tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0)
        tbl.row();

        // + Save Button with event handler
        btnWinOptSave = new TextButton("Save", skinLibgdx)
        tbl.add(btnWinOptSave).padRight(30)
        btnWinOptSave.addListener({ ChangeListener.ChangeEvent event, Actor actor -> onSaveClicked() } as ChangeListener)

        // + Cancel Button with event handler
        btnWinOptCancel = new TextButton("Cancel", skinLibgdx)
        tbl.add(btnWinOptCancel)
        btnWinOptCancel.addListener({ ChangeListener.ChangeEvent event, Actor actor -> onCancelClicked() } as ChangeListener)

        return tbl
    }

    private Table buildOptionsWindowLayer() {
        winOptions = new Window("Options", skinLibgdx)

        // + Audio Settings: Sound/Music CheckBox and Volume Slider
        winOptions.add(buildOptWinAudioSettings()).row()

        // + Character Skin: Selection Box (White, Gray, Brown)
        winOptions.add(buildOptWinSkinSelection()).row()

        // + Debug: Show FPS Counter
        winOptions.add(buildOptWinDebug()).row()

        // + Separator and Buttons (Save, Cancel)
        winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0)

        // Make options window slightly transparent
        winOptions.setColor(1, 1, 1, 0.8f)

        showOptionsWindow(false, false)

        if (debugEnabled) winOptions.debug()

        // Let TableLayout recalculate widget sizes and positions
        winOptions.pack()

        // Move options window to bottom right corner
        winOptions.setPosition((VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50) as float, 50f)

        return winOptions
    }
}
