package hzt.controller.sub_pane

import hzt.controller.FXMLController
import hzt.controller.scene.MainSceneController
import hzt.controller.scene.SceneController
import hzt.model.DoublePendulum
import hzt.model.PropertiesParser
import hzt.model.appearance.Resource
import hzt.model.trace.TraceXY
import hzt.service.StandardBackgroundService
import hzt.service.StandardThemeService
import hzt.service.inteface.BackgroundService
import hzt.service.inteface.ThemeService
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Collections
import java.util.Optional
import java.util.function.Consumer
import javafx.animation.Transition
import javafx.beans.property.ObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.ToggleButton
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Duration

class AppearanceController(
    private val mainSceneController: MainSceneController,
    doublePendulums: Collection<DoublePendulum>,
    propertiesParser: PropertiesParser
) : FXMLController("appearancePane.fxml") {
    @FXML
    private lateinit var traceColorPicker: ColorPicker

    @FXML
    private lateinit var opacityStageButton: ToggleButton

    @FXML
    private lateinit var backgroundCombobox: ComboBox<Resource>

    @FXML
    private lateinit var themeCombobox: ComboBox<Resource>

    @FXML
    private lateinit var fullScreenButton: ToggleButton

    @FXML
    private lateinit var backgroundColorPicker: ColorPicker

    @FXML
    private lateinit var pendulumColorPicker: ColorPicker

    @FXML
    private lateinit var pendulumMassColorPicker: ColorPicker

    private val themeService: ThemeService = StandardThemeService()
    private val backgroundService: BackgroundService = StandardBackgroundService()
    private val doublePendulums: Collection<DoublePendulum> = Collections.unmodifiableCollection(doublePendulums)
    private val stageOpacity = propertiesParser.parsedDoubleProp("stage_opacity", 0.8)


    init {
        configureComboBoxes()
    }

    fun configureColorPickers() {
        backgroundColorPicker.value = INIT_BG_COLOR
        pendulumMassColorPicker.value = INIT_HINGE_COLOR
        pendulumColorPicker.value = INIT_ARM_COLOR
        traceColorPicker.value = INIT_TRACE_COLOR
        doublePendulums.forEach(this::bindColorProperties)
    }

    private fun bindColorProperties(doublePendulum: DoublePendulum) {
        doublePendulum.massColorProperty().bind(pendulumMassColorPicker.valueProperty())
        val armMaterial = doublePendulum.armMaterial
        armMaterial.diffuseColorProperty().bind(pendulumColorPicker.valueProperty())
        armMaterial.specularColorProperty().bind(pendulumColorPicker.valueProperty())
    }

    fun bindTraceColorToTraceColorPicker(traceXY: TraceXY) {
        traceXY.colorProperty().bind(traceColorPicker.valueProperty())
    }

    private fun configureComboBoxes() {
        val themes = themeService.themes.sortedBy(Resource::name)

        themeCombobox.items.addAll(themes)
        themeService.currentThemeProperty().bind(themeCombobox.valueProperty())
        themeService.styleSheetProperty().addListener { _, _, newVal -> changeStyle(newVal) }
        themeCombobox.value = ThemeService.DEFAULT_THEME

        val backgrounds = backgroundService.resources.sortedBy(Resource::name)

        backgroundCombobox.items.addAll(backgrounds)
        backgroundCombobox.value = StandardBackgroundService.NO_PICTURE
    }

    private fun changeStyle(newVal: String?) {
        val sceneManager = mainSceneController.sceneManager
        val sceneControllers: Collection<SceneController> = sceneManager.sceneControllerMap.values
        for (sceneController in sceneControllers) {
            val styleSheets = sceneController.scene.stylesheets
            styleSheets.removeIf { styleSheets.isNotEmpty() }
            if (newVal != null) {
                styleSheets.add(newVal)
            }
        }
    }

    fun configureStageControlButtons(stage: Stage) {
        fullScreenButton.onAction = EventHandler { stage.isFullScreen = !stage.isFullScreen }
        stage.fullScreenProperty().addListener { _, _, n -> fullScreenButton.isSelected = n }
        stage.addEventFilter(KeyEvent.KEY_PRESSED) { key: KeyEvent -> setFullScreenWhenF11Pressed(stage, key) }
        bindStageOpacityToOpacityButton(stage)
    }

    private fun bindStageOpacityToOpacityButton(stage: Stage) {
        opacityStageButton.selectedProperty().addListener { _, _, n ->
            stage.opacity = if (java.lang.Boolean.TRUE == n) stageOpacity else 1.0
        }
    }

    @FXML
    private fun backgroundComboBoxAction() {
        Optional.of(backgroundCombobox.value.file)
            .filter { file: File -> file.path.isNotBlank() }
            .map { file: File -> toInputStream(file) }
            .ifPresentOrElse({ inputStream: InputStream -> this.changeBackgroundImage(inputStream) }, { this.backgroundColorPickerAction() })
    }

    private fun changeBackgroundImage(inputStream: InputStream) {
        val animationPane = mainSceneController.animationPane
        val background = Background(
            BackgroundImage(
                Image(inputStream),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize(
                    animationPane.width, animationPane.height,
                    false, false, false, true
                )
            )
        )
        animationPane.background = background
    }

    @FXML
    private fun backgroundColorPickerAction() {
        mainSceneController.animationPane.background = Background(
            BackgroundFill(backgroundColorPicker.value, CornerRadii.EMPTY, Insets.EMPTY)
        )
        backgroundCombobox.value = StandardBackgroundService.NO_PICTURE
    }

    fun highlightSelectedPendulum(pendulum: DoublePendulum) {
        playColorTransition(pendulum.massColorProperty(), pendulumMassColorPicker.valueProperty())
        playColorTransition(pendulum.armMaterial.diffuseColorProperty(), pendulumColorPicker.valueProperty())
    }

    private fun playColorTransition(colorObjectProperty: ObjectProperty<Color>, bound: ObjectProperty<Color>) {
        val noOtherColorTransitionPlaying = colorObjectProperty.isBound
        if (noOtherColorTransitionPlaying) {
            val current = colorObjectProperty.get()
            val colorTransition = ColorTransition(Duration.seconds(1.0), current.invert(), current, colorObjectProperty::set)
            colorObjectProperty.unbind()
            colorTransition.onFinished = EventHandler { colorObjectProperty.bind(bound) }
            colorTransition.play()
        }
    }

    private class ColorTransition(duration: Duration?, fromColor: Color, toColor: Color, colorConsumer: Consumer<Color>) : Transition() {
        private val fromColor: Color
        private val toColor: Color
        private val colorObjectProperty: Consumer<Color>

        init {
            cycleDuration = duration
            this.fromColor = fromColor
            this.toColor = toColor
            this.colorObjectProperty = colorConsumer
        }

        override fun interpolate(frac: Double) {
            colorObjectProperty.accept(fromColor.interpolate(toColor, frac))
        }
    }

    companion object {
        val INIT_BG_COLOR: Color = Color.WHEAT
        private val INIT_HINGE_COLOR: Color = Color.ORANGE
        private val INIT_ARM_COLOR: Color = Color.RED
        private val INIT_TRACE_COLOR: Color = Color.GREEN

        private fun setFullScreenWhenF11Pressed(stage: Stage, keyEvent: KeyEvent) {
            if (keyEvent.code == KeyCode.F11) {
                stage.isFullScreen = !stage.isFullScreen
            }
        }

        private fun toInputStream(file: File): InputStream {
            try {
                return FileInputStream(file)
            } catch (e: IOException) {
                throw IllegalStateException(e)
            }
        }
    }
}
