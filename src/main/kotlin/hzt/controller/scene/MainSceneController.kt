package hzt.controller.scene

import hzt.controller.SceneManager
import hzt.controller.sub_pane.AppearanceController
import hzt.controller.sub_pane.StatisticsController
import hzt.engine.DoublePendulumCalculator
import hzt.model.*
import hzt.service.StandardAnimationService
import hzt.service.StandardMouseControlService
import hzt.service.inteface.AnimationService
import hzt.service.inteface.MouseControlService
import hzt.utils.FxUtils
import hzt.utils.Integrator
import hzt.utils.StringUtils
import java.time.Clock
import java.time.Duration
import java.time.LocalTime
import java.util.ArrayDeque
import java.util.Deque
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.geometry.Dimension2D
import javafx.geometry.Insets
import javafx.geometry.Point3D
import javafx.scene.Group
import javafx.scene.SceneAntialiasing
import javafx.scene.SubScene
import javafx.scene.control.ComboBox
import javafx.scene.control.Slider
import javafx.scene.control.Tab
import javafx.scene.control.ToggleButton
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.DrawMode
import javafx.scene.shape.Shape3D
import javafx.scene.transform.Translate
import kotlin.math.max
import kotlin.streams.asSequence

class MainSceneController(sceneManager: SceneManager, propertiesParser: PropertiesParser, clock: Clock) :
    SceneController(SceneManager.Scene.MAIN_SCENE.fxmlFileName, propertiesParser, sceneManager, clock) {

    @FXML
    private lateinit var sliderPane: AnchorPane

    @FXML
    private lateinit var appearanceTab: Tab

    @FXML
    private lateinit var statisticsTab: Tab

    @FXML
    lateinit var animationPane: AnchorPane

    @FXML
    private lateinit var traceModeCombobox: ComboBox<String>

    @FXML
    private lateinit var integratorCombobox: ComboBox<Integrator<DoublePendulumCalculator>>

    @FXML
    private lateinit var pauseButton: ToggleButton

    @FXML
    private lateinit var showAxisButton: ToggleButton

    @FXML
    private lateinit var pendulumCountSlider: Slider

    @FXML
    private lateinit var lowerPendulumLengthSlider: Slider

    @FXML
    private lateinit var upperMassSlider: Slider

    @FXML
    private lateinit var angle1Slider: Slider

    @FXML
    private lateinit var lowerMassSlider: Slider

    @FXML
    private lateinit var angle2Slider: Slider

    @FXML
    private lateinit var upperPendulumLengthSlider: Slider

    @FXML
    private lateinit var frictionSlider: Slider

    @FXML
    private lateinit var cameraMaxVelocitySlider: Slider

    @FXML
    private lateinit var cameraMaxAccelerationSlider: Slider

    @FXML
    private lateinit var cameraFieldOfViewSlider: Slider

    @FXML
    private lateinit var maxTraceLengthSlider: Slider

    @FXML
    private lateinit var vAngular2Slider: Slider

    @FXML
    private lateinit var vAngular1Slider: Slider

    private val subSceneRoot = Group()
    private val doublePendulums = buildDoublePendulums()
    private val selectedDoublePendulum: ObjectProperty<DoublePendulum> = SimpleObjectProperty()

    private val coordinateSystem3D: CoordinateSystem3D = CylinderCoordinateSystem3D()

    private val subSceneRootMouseControlService: MouseControlService = StandardMouseControlService(subSceneRoot)

    private val statisticsController = StatisticsController()
    private val appearanceController: AppearanceController
    private val animationService: AnimationService = StandardAnimationService()

    private val subScene3D = SubScene(subSceneRoot, 0.0, 0.0, true, SceneAntialiasing.BALANCED)

    private val cameraPlatform = MovableCameraPlatform()

    private val initMass1 = propertiesParser.parsedDoubleProp("init_mass1", 10.0)
    private val initMass2 = propertiesParser.parsedDoubleProp("init_mass2", 100.0)
    private val initUpperArmLength = propertiesParser.parsedDoubleProp("init_upper_arm_length", 100.0)
    private val initLowerArmLength = propertiesParser.parsedDoubleProp("init_lower_arm_length", 100.0)
    private val initAngle1 = propertiesParser.parsedDoubleProp("init_angle1", 30.0)
    private val initAngle2 = propertiesParser.parsedDoubleProp("init_angle2", 30.0)
    private val deltaAngle1 = propertiesParser.parsedDoubleProp("delta_angle1", 0.1)
    private val initWorldToCameraAngleX = propertiesParser.parsedDoubleProp("init_angle_x", 0.0)
    private val initWorldToCameraAngleY = propertiesParser.parsedDoubleProp("init_angle_y", 0.0)

    init {
        selectedDoublePendulum.addListener { _, _, n -> statisticsController.setPendulumName(n.name) }
        selectedDoublePendulum.set(doublePendulums.peekLast())
        animationPane.children.add(subScene3D)
        statisticsTab.content = statisticsController.root
        appearanceController = AppearanceController(this, propertiesParser).also { appearanceTab.content = it.root }
    }

    public override fun setup() {
        configureAnimationPane(animationPane)
        configureCameraPlatform(cameraPlatform)
        configureSubScene(subScene3D, animationPane)
        setInitValuesSliders()
        configureComboBoxes()
        configureAppearanceController()
        configureDoublePendulums()
        changeSliderBindings(null, selectedDoublePendulum.get())
        scene.addEventFilter(KeyEvent.KEY_TYPED, ::wireFrameWhenZPressed)
        MainSceneControllers.configureResetAllInstances(this, scene)
        subSceneRootMouseControlService.initMouseControl(animationPane)

        subSceneRootMouseControlService.setOrientation(initWorldToCameraAngleX, initWorldToCameraAngleY)
        subSceneRoot.children.add(coordinateSystem3D)
        doublePendulums.forEach { subSceneRoot.children.add(it) }
        coordinateSystem3D.visibleProperty().bind(showAxisButton.selectedProperty())
        pauseButton.selectedProperty()
            .addListener { _, _, isSelected -> pauseSimButtonAction(java.lang.Boolean.TRUE == isSelected) }
        animationService.addLoopHandlersToTimelines(true, { animationService.run(doublePendulums, cameraPlatform) }, { updateStatisticsLoop() })
        FxUtils.allDescendents(sliderPane).asSequence()
            .filterIsInstance<Slider>()
            .forEach { it.valueProperty().addListener { _, _, _ -> updatePendulumPositionsIfPaused() }
            }
    }

    private fun updatePendulumPositionsIfPaused() {
        if (pauseButton.isSelected) {
            doublePendulums.forEach(DoublePendulum::update)
        }
    }

    private fun buildDoublePendulums(): Deque<DoublePendulum> {
        val initNrOfSliders = max(propertiesParser.parsedIntProp("init_number_of_pendulums", 1), 1)
        pendulumCountSlider.value = initNrOfSliders.toDouble()
        pendulumCountSlider.valueProperty().addListener { _, _, n ->
            val target = n.toInt()
            while (doublePendulums.size != target) {
                val size = doublePendulums.size
                if (size < target) {
                    val pendulum = createDoublePendulum(size + 1)
                    doublePendulums.push(pendulum)
                    appearanceController.bindColorProperties(pendulum)
                    configureDoublePendulum(pendulum, 0.0)
                    subSceneRoot.children.add(pendulum)
                } else {
                    val pendulum = doublePendulums.pop()
                    subSceneRoot.children.remove(pendulum)
                }

            }
        }
        return (1..initNrOfSliders)
            .map(::createDoublePendulum)
            .fold(ArrayDeque()) { c, t -> c.also { c.push(t) } }
    }

    private fun createDoublePendulum(pendulumNr: Int): DoublePendulum {
        val doublePendulum = DoublePendulum("Pendulum $pendulumNr")
        doublePendulum.translateZ = pendulumNr * Z_SPACING
        doublePendulum.onMouseClicked = EventHandler(::selectPendulum)
        return doublePendulum
    }

    private fun selectPendulum(e: MouseEvent) {
        val clicked = e.source as DoublePendulum
        val current = selectedDoublePendulum.get()
        selectedDoublePendulum.set(clicked)
        appearanceController.highlightSelectedPendulum(clicked)
        changeSliderBindings(current, clicked)
    }

    private fun configureDoublePendulums() {
        var deltaAngle = 0.0
        for (doublePendulum in doublePendulums) {
            configureDoublePendulum(doublePendulum, deltaAngle)
            deltaAngle += deltaAngle1
        }
    }

    private fun configureAppearanceController() {
        appearanceController.configureColorPickers(doublePendulums)
        appearanceController.configureStageControlButtons(sceneManager.stage)
    }

    private fun configureCameraPlatform(cameraPlatform: MovableCameraPlatform) {
        cameraPlatform.setTranslate(initCameraTranslation())
        cameraPlatform.addKeyControls(scene)

        cameraPlatform.camera.fieldOfViewProperty().bindBidirectional(cameraFieldOfViewSlider.valueProperty())
        cameraPlatform.maxVelocityProperty().bindBidirectional(cameraMaxVelocitySlider.valueProperty())
        cameraPlatform.maxAccelerationProperty().bindBidirectional(cameraMaxAccelerationSlider.valueProperty())
    }

    private fun configureSubScene(subScene: SubScene, animationPane: Pane) {
        subScene.widthProperty().bind(animationPane.widthProperty())
        subScene.heightProperty().bind(animationPane.heightProperty())
        subScene.camera = cameraPlatform.camera
    }

    private fun wireFrameWhenZPressed(e: KeyEvent) {
        if ("z" == e.character) {
            FxUtils.allDescendents(subSceneRoot, Shape3D::class.java).forEach(::setDrawMode)
        }
    }

    private fun setDrawMode(shape3D: Shape3D) {
        if (!shape3D.drawModeProperty().isBound) {
            shape3D.drawMode = if (DrawMode.LINE == shape3D.drawMode) DrawMode.FILL else DrawMode.LINE
        }
    }

    private fun configureAnimationPane(animationPane: AnchorPane?) {
        val animationPaneWidthProp = propertiesParser.parsedIntProp("init_animation_pane_width", 640)
        val animationPaneHeightProp = propertiesParser.parsedIntProp("init_animation_pane_height", 640)
        val paneDimension = Dimension2D(animationPaneWidthProp.toDouble(), animationPaneHeightProp.toDouble())
        animationPane!!.setPrefSize(paneDimension.width, paneDimension.height)
        animationPane.background = Background(BackgroundFill(AppearanceController.INIT_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY))
    }

    private fun configureComboBoxes() {
        val integrators = integratorCombobox.items
        val rungeKutta = Integrator("Runge Kutta 4 integration", DoublePendulumCalculator::integrateByRungeKutta4)
        integrators.addAll(Integrator("Euler integration", DoublePendulumCalculator::integrateByEuler), rungeKutta)
        integratorCombobox.value = rungeKutta

        val traceModes = traceModeCombobox.items
        traceModes.addAll(DoublePendulum.BALL_TRACE, DoublePendulum.ROUND_LINK_TRACE, DoublePendulum.NO_TRACE, DoublePendulum.SQUARE_LINK_TRACE)
        val initTraceType = StringUtils.toOnlyFirstLetterUpperCase(propertiesParser.property("init_trace_type", ""))
        val finalInitTraceType = if (traceModes.contains(initTraceType)) initTraceType else DoublePendulum.BALL_TRACE
        traceModeCombobox.value = finalInitTraceType
    }

    private fun configureDoublePendulum(doublePendulum: DoublePendulum, deltaAngle: Double) {
        setDoublePendulumCalculatorParams(doublePendulum.calculator, deltaAngle)
        bindMassesAndArmLengths(doublePendulum)
        doublePendulum.frictionProperty().bind(frictionSlider.valueProperty())
        doublePendulum.traceTypeProperty().bind(traceModeCombobox.valueProperty())
        doublePendulum.integratorProperty().bind(integratorCombobox.valueProperty())

        val traceTypes = doublePendulum.traceTypes()
        traceTypes.forEach { it.maxLengthProperty().bind(maxTraceLengthSlider.valueProperty()) }
        traceTypes.forEach(appearanceController::bindTraceColorToTraceColorPicker)
    }

    private fun setDoublePendulumCalculatorParams(calculator: DoublePendulumCalculator, deltaAngle: Double) {
        calculator.resetVAndAAngular()
        calculator.angle1Property.set(Math.toRadians(initAngle1 + deltaAngle))
        calculator.angle2Property.set(Math.toRadians(initAngle2))
    }

    private fun bindMassesAndArmLengths(doublePendulum: DoublePendulum) {
        val calculator = doublePendulum.calculator
        calculator.upperMassProperty.bind(upperMassSlider.valueProperty())
        calculator.lowerMassProperty.bind(lowerMassSlider.valueProperty())
        doublePendulum.upperArmLengthProperty().bind(upperPendulumLengthSlider.valueProperty())
        doublePendulum.lowerArmLengthProperty().bind(lowerPendulumLengthSlider.valueProperty())
    }

    private fun changeSliderBindings(current: DoublePendulum?, next: DoublePendulum) {
        if (current != null) {
            current.angle1Property().unbindBidirectional(angle1Slider.valueProperty())
            current.angle2Property().unbindBidirectional(angle2Slider.valueProperty())
            current.vAngular1Property().unbindBidirectional(vAngular1Slider.valueProperty())
            current.vAngular2Property().unbindBidirectional(vAngular2Slider.valueProperty())
        }
        angle1Slider.valueProperty().bindBidirectional(next.angle1Property())
        angle2Slider.valueProperty().bindBidirectional(next.angle2Property())
        vAngular1Slider.valueProperty().bindBidirectional(next.vAngular1Property())
        vAngular2Slider.valueProperty().bindBidirectional(next.vAngular2Property())
    }

    private fun updateStatisticsLoop() {
        val runTimeSim = Duration.between(startTimeScene, LocalTime.now(clock))
        statisticsController.updatePendulumStatistics(selectedDoublePendulum.get(), subSceneRootMouseControlService)
        statisticsController.updateGlobalStatistics(runTimeSim)
        statisticsController.updateCameraPlatformStatistics(cameraPlatform)
    }

    private fun setInitValuesSliders() {
        upperMassSlider.value = initMass1
        lowerMassSlider.value = initMass2
        upperPendulumLengthSlider.value = initUpperArmLength
        lowerPendulumLengthSlider.value = initLowerArmLength
        setInitValueAngleSliders()
        frictionSlider.value = propertiesParser.parsedDoubleProp("init_friction", 0.001)
        maxTraceLengthSlider.value = INIT_MAX_TRACE_LENGTH
        cameraFieldOfViewSlider.value = INIT_CAMERA_FOV
        cameraMaxVelocitySlider.value = INIT_MAX_CAMERA_VELOCITY
    }

    private fun setInitValueAngleSliders() {
        angle1Slider.min = -Math.PI
        angle1Slider.value = Math.toRadians(initAngle1)
        angle1Slider.max = Math.PI
        angle2Slider.min = -Math.PI
        angle2Slider.value = Math.toRadians(initAngle2)
        angle2Slider.max = Math.PI
        vAngular1Slider.min = -MAX_ANGULAR_VELOCITY
        vAngular1Slider.max = MAX_ANGULAR_VELOCITY
        vAngular2Slider.min = -MAX_ANGULAR_VELOCITY
        vAngular2Slider.max = MAX_ANGULAR_VELOCITY
    }

    private fun pauseSimButtonAction(isSelected: Boolean) {
        if (isSelected) {
            animationService.pauseAnimationTimeline()
        } else {
            animationService.startAnimationTimeline()
        }
    }

    fun togglePauseButton() {
        pauseButton.isSelected = !pauseButton.isSelected
    }

    @FXML
    fun resetButtonAction() {
        setInitValuesSliders()
        setParamsDoublePendulums()
    }

    private fun setParamsDoublePendulums() {
        for ((counter, doublePendulum) in doublePendulums.withIndex()) {
            setDoublePendulumCalculatorParams(doublePendulum.calculator, counter * deltaAngle1)
        }
    }

    @FXML
    private fun clearTraceButtonAction() {
        doublePendulums.forEach(DoublePendulum::clearTrace)
    }

    private fun initCameraTranslation(): Translate {
        return Translate(0.0, 0.0, INIT_CAMERA_Z_PENDULUMS)
    }

    @FXML
    private fun recenterButtonAction() {
        cameraPlatform.setTranslate(initCameraTranslation())
        cameraPlatform.camera.fieldOfView = INIT_CAMERA_FOV
        subSceneRootMouseControlService.setOrientation(initWorldToCameraAngleX, initWorldToCameraAngleY)
        subSceneRootMouseControlService.setTargetTranslation(Point3D.ZERO)
    }

    @FXML
    private fun showAbout() {
        sceneManager.setupScene(SceneManager.Scene.ABOUT_SCENE)
    }

    companion object {
        private const val Z_SPACING = 30.0
        private const val MAX_ANGULAR_VELOCITY = Math.PI / 4.0
        private const val INIT_MAX_TRACE_LENGTH = 500.0
        private const val INIT_CAMERA_Z_PENDULUMS = -500.0
        private const val INIT_CAMERA_FOV = 30.0
        private const val INIT_MAX_CAMERA_VELOCITY = 100.0
    }
}
