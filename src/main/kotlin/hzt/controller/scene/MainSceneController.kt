package hzt.controller.scene;

import hzt.controller.SceneManager;
import hzt.controller.sub_pane.AppearanceController;
import hzt.controller.sub_pane.StatisticsController;
import hzt.model.CoordinateSystem3D;
import hzt.model.CylinderCoordinateSystem3D;
import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import hzt.model.PropertiesParser;
import hzt.model.trace.TraceXY;
import hzt.service.StandardAnimationService;
import hzt.service.StandardMouseControlService;
import hzt.service.inteface.AnimationService;
import hzt.service.inteface.MouseControlService;
import hzt.utils.FxUtils;
import hzt.utils.Integrator;
import hzt.utils.StringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Translate;

import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hzt.controller.SceneManager.Scene.ABOUT_SCENE;
import static hzt.controller.SceneManager.Scene.MAIN_SCENE;
import static hzt.controller.sub_pane.AppearanceController.INIT_BG_COLOR;
import static hzt.model.DoublePendulum.BALL_TRACE;
import static hzt.model.DoublePendulum.NO_TRACE;
import static hzt.model.DoublePendulum.ROUND_LINK_TRACE;
import static hzt.model.DoublePendulum.SQUARE_LINK_TRACE;

public final class MainSceneController extends SceneController {

    private static final double Z_SPACING = 30.0;
    private static final double MAX_ANGULAR_VELOCITY = Math.PI / 4.0;
    private static final double INIT_MAX_TRACE_LENGTH = 500.0;
    private static final double INIT_CAMERA_Z_PENDULUMS = -500;
    private static final double INIT_CAMERA_FOV = 30;
    private static final double INIT_MAX_CAMERA_VELOCITY = 100;

    @FXML
    private AnchorPane sliderPane;
    @FXML
    private Tab appearanceTab;
    @FXML
    private Tab statisticsTab;
    @FXML
    private AnchorPane animationPane;
    @FXML
    private ComboBox<String> traceModeCombobox;
    @FXML
    private ComboBox<Integrator<DoublePendulum>> integratorCombobox;
    @FXML
    private ToggleButton pauseButton;
    @FXML
    private ToggleButton showAxisButton;
    @FXML
    private Slider lowerPendulumLengthSlider;
    @FXML
    private Slider upperMassSlider;
    @FXML
    private Slider angle1Slider;
    @FXML
    private Slider lowerMassSlider;
    @FXML
    private Slider angle2Slider;
    @FXML
    private Slider upperPendulumLengthSlider;
    @FXML
    private Slider frictionSlider;
    @FXML
    private Slider cameraMaxVelocitySlider;
    @FXML
    private Slider cameraMaxAccelerationSlider;
    @FXML
    private Slider cameraFieldOfViewSlider;
    @FXML
    private Slider maxTraceLengthSlider;
    @FXML
    private Slider vAngular2Slider;
    @FXML
    private Slider vAngular1Slider;

    private final Group subSceneRoot = new Group();
    private final Deque<DoublePendulum> doublePendulums = buildDoublePendulums();
    private final ObjectProperty<DoublePendulum> selectedDoublePendulum;

    private final CoordinateSystem3D coordinateSystem3D = new CylinderCoordinateSystem3D();

    private final MouseControlService subSceneRootMouseControlService = new StandardMouseControlService(subSceneRoot);

    private final StatisticsController statisticsController;
    private final AppearanceController appearanceController;
    private final AnimationService animationService = new StandardAnimationService();

    private final SubScene subScene3D = new SubScene(subSceneRoot, 0.0, 0.0, true, SceneAntialiasing.BALANCED);

    private final MovableCameraPlatform cameraPlatform = new MovableCameraPlatform();

    private final double initMass1 = propertiesParser.parsedDoubleProp("init_mass1", 10.0);
    private final double initMass2 = propertiesParser.parsedDoubleProp("init_mass2", 100.0);
    private final double initUpperArmLength = propertiesParser.parsedDoubleProp("init_upper_arm_length", 100.0);
    private final double initLowerArmLength = propertiesParser.parsedDoubleProp("init_lower_arm_length", 100.0);
    private final double initAngle1 = propertiesParser.parsedDoubleProp("init_angle1", 30.0);
    private final double initAngle2 = propertiesParser.parsedDoubleProp("init_angle2", 30.0);
    private final double deltaAngle1 = propertiesParser.parsedDoubleProp("delta_angle1", 0.1);
    private final double initWorldToCameraAngleX = propertiesParser.parsedDoubleProp("init_angle_x", 0);
    private final double initWorldToCameraAngleY = propertiesParser.parsedDoubleProp("init_angle_y", 0);

    public MainSceneController(SceneManager sceneManager, PropertiesParser propertiesParser, Clock clock) throws IOException {
        super(MAIN_SCENE.getFxmlFileName(), propertiesParser, sceneManager, clock);
        selectedDoublePendulum = new SimpleObjectProperty<>();
        statisticsController = new StatisticsController();
        selectedDoublePendulum.addListener((s, w, n) -> statisticsController.setPendulumName(n.getName()));
        selectedDoublePendulum.set(doublePendulums.peek());
        animationPane.getChildren().add(subScene3D);
        statisticsTab.setContent(statisticsController.getRoot());
        appearanceController = new AppearanceController(this, doublePendulums, propertiesParser);
        appearanceTab.setContent(appearanceController.getRoot());
    }

    @Override
    public void setup() {
        configureAnimationPane(animationPane);
        configureCameraPlatform(cameraPlatform);
        configureSubScene(subScene3D, animationPane);
        setInitValuesSliders();
        configureComboBoxes();
        configureAppearanceController();
        configureDoublePendulums();
        changeSliderBindings(null, selectedDoublePendulum.get());
        scene.addEventFilter(KeyEvent.KEY_TYPED, this::wireFrameWhenZPressed);
        MainSceneControllers.configureResetAllInstances(this, scene);
        subSceneRootMouseControlService.initMouseControl(animationPane);

        subSceneRootMouseControlService.setOrientation(initWorldToCameraAngleX, initWorldToCameraAngleY);
        subSceneRoot.getChildren().add(coordinateSystem3D);
        doublePendulums.forEach(doublePendulum -> subSceneRoot.getChildren().add(doublePendulum));
        coordinateSystem3D.visibleProperty().bind(showAxisButton.selectedProperty());
        pauseButton.selectedProperty().addListener((o, c, isSelected) -> pauseSimButtonAction(Boolean.TRUE.equals(isSelected)));
        animationService.addLoopHandlersToTimelines(true,
                e -> animationService.run(doublePendulums, cameraPlatform),
                e -> updateStatisticsLoop());
        FxUtils.allDescendents(sliderPane)
                .filter(Slider.class::isInstance)
                .map(Slider.class::cast)
                .forEach(s -> s.valueProperty().addListener((o, a, v) -> updatePendulumPositionsIfPaused()));
    }

    private void updatePendulumPositionsIfPaused() {
        if (pauseButton.isSelected()) {
            doublePendulums.forEach(DoublePendulum::updatePendulumPosition);
        }
    }

    private Deque<DoublePendulum> buildDoublePendulums() {
        var nrOfPendulums = Math.max(propertiesParser.parsedIntProp("number_of_pendulums", 1), 1);
        return IntStream.rangeClosed(1, nrOfPendulums)
                .mapToObj(this::createDoublePendulum)
                .collect(Collectors.toCollection(ArrayDeque::new));
    }

    private DoublePendulum createDoublePendulum(int pendulumNr) {
        var doublePendulum = new DoublePendulum("Pendulum " + pendulumNr);
        doublePendulum.setTranslateZ(pendulumNr * Z_SPACING);
        doublePendulum.setOnMouseClicked(this::selectPendulum);
        return doublePendulum;
    }

    private void selectPendulum(MouseEvent e) {
        DoublePendulum clicked = (DoublePendulum) e.getSource();
        DoublePendulum current = selectedDoublePendulum.get();
        selectedDoublePendulum.set(clicked);
        appearanceController.highlightSelectedPendulum(clicked);
        changeSliderBindings(current, clicked);
    }

    private void configureDoublePendulums() {
        var deltaAngle = 0.0;
        for (DoublePendulum doublePendulum : doublePendulums) {
            configureDoublePendulum(doublePendulum, deltaAngle);
            deltaAngle += deltaAngle1;
        }
    }

    private void configureAppearanceController() {
        appearanceController.configureColorPickers();
        appearanceController.configureStageControlButtons(sceneManager.getStage());
    }

    private void configureCameraPlatform(MovableCameraPlatform cameraPlatform) {
        cameraPlatform.setTranslate(initCameraTranslation());
        cameraPlatform.addKeyControls(scene);

        cameraPlatform.getCamera().fieldOfViewProperty().bindBidirectional(cameraFieldOfViewSlider.valueProperty());
        cameraPlatform.maxVelocityProperty().bindBidirectional(cameraMaxVelocitySlider.valueProperty());
        cameraPlatform.maxAccelerationProperty().bindBidirectional(cameraMaxAccelerationSlider.valueProperty());
    }

    private void configureSubScene(SubScene subScene, Pane animationPane) {
        subScene.widthProperty().bind(animationPane.widthProperty());
        subScene.heightProperty().bind(animationPane.heightProperty());
        subScene.setCamera(cameraPlatform.getCamera());
    }

    private void wireFrameWhenZPressed(KeyEvent e) {
        if ("z".equals(e.getCharacter())) {
            FxUtils.allDescendents(subSceneRoot, Shape3D.class).forEach(this::setDrawMode);
        }
    }

    void setDrawMode(Shape3D shape3D) {
        if (!shape3D.drawModeProperty().isBound()) {
            shape3D.setDrawMode(DrawMode.LINE == shape3D.getDrawMode() ? DrawMode.FILL : DrawMode.LINE);
        }
    }

    private void configureAnimationPane(AnchorPane animationPane) {
        int animationPaneWidthProp = propertiesParser.parsedIntProp("init_animation_pane_width", 640);
        int animationPaneHeightProp = propertiesParser.parsedIntProp("init_animation_pane_height", 640);
        Dimension2D paneDimension = new Dimension2D(animationPaneWidthProp, animationPaneHeightProp);
        animationPane.setPrefSize(paneDimension.getWidth(), paneDimension.getHeight());
        animationPane.setBackground(new Background(new BackgroundFill(INIT_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void configureComboBoxes() {
        final ObservableList<Integrator<DoublePendulum>> integrators = integratorCombobox.getItems();
        final var rungeKutta = new Integrator<>("Runge Kutta 4 integration", DoublePendulum::integrateByRungeKutta4);
        integrators.addAll(List.of(new Integrator<>("Euler integration", DoublePendulum::integrateByEuler), rungeKutta));
        integratorCombobox.setValue(rungeKutta);

        ObservableList<String> traceModes = traceModeCombobox.getItems();
        traceModes.addAll(BALL_TRACE, ROUND_LINK_TRACE, NO_TRACE, SQUARE_LINK_TRACE);
        String initTraceType = StringUtils.toOnlyFirstLetterUpperCase(propertiesParser.property("init_trace_type", ""));
        String finalInitTraceType = traceModes.contains(initTraceType) ? initTraceType : BALL_TRACE;
        traceModeCombobox.setValue(finalInitTraceType);
    }

    private void configureDoublePendulum(DoublePendulum doublePendulum, double deltaAngle) {
        setDoublePendulumParams(doublePendulum, deltaAngle);
        bindMassesAndArmLengths(doublePendulum);
        doublePendulum.frictionProperty().bind(frictionSlider.valueProperty());
        doublePendulum.traceTypeProperty().bind(traceModeCombobox.valueProperty());
        doublePendulum.integratorProperty().bind(integratorCombobox.valueProperty());

        final Collection<TraceXY> traceTypes = doublePendulum.traceTypes();
        traceTypes.forEach(trace -> trace.maxLengthProperty().bind(maxTraceLengthSlider.valueProperty()));
        traceTypes.forEach(appearanceController::bindTraceColorToTraceColorPicker);
    }

    private void setDoublePendulumParams(DoublePendulum doublePendulum, double deltaAngle) {
        doublePendulum.resetVAndAAngular();
        doublePendulum.angle1Property().set(Math.toRadians(initAngle1 + deltaAngle));
        doublePendulum.angle2Property().set(Math.toRadians(initAngle2));
    }

    private void bindMassesAndArmLengths(DoublePendulum doublePendulum) {
        doublePendulum.upperPendulum.massProperty().bind(upperMassSlider.valueProperty());
        doublePendulum.lowerPendulum.massProperty().bind(lowerMassSlider.valueProperty());
        doublePendulum.upperArmLengthProperty().bind(upperPendulumLengthSlider.valueProperty());
        doublePendulum.lowerArmLengthProperty().bind(lowerPendulumLengthSlider.valueProperty());
    }

    private void changeSliderBindings(DoublePendulum current, DoublePendulum next) {
        if (current != null) {
            current.angle1Property().unbindBidirectional(angle1Slider.valueProperty());
            current.angle2Property().unbindBidirectional(angle2Slider.valueProperty());
            current.vAngular1Property().unbindBidirectional(vAngular1Slider.valueProperty());
            current.vAngular2Property().unbindBidirectional(vAngular2Slider.valueProperty());
        }
        angle1Slider.valueProperty().bindBidirectional(next.angle1Property());
        angle2Slider.valueProperty().bindBidirectional(next.angle2Property());
        vAngular1Slider.valueProperty().bindBidirectional(next.vAngular1Property());
        vAngular2Slider.valueProperty().bindBidirectional(next.vAngular2Property());
    }

    private void updateStatisticsLoop() {
        Duration runTimeSim = Duration.between(startTimeScene, LocalTime.now(clock));
        statisticsController.updatePendulumStatistics(selectedDoublePendulum.get(), subSceneRootMouseControlService);
        statisticsController.updateGlobalStatistics(runTimeSim);
        statisticsController.updateCameraPlatformStatistics(cameraPlatform);
    }

    private void setInitValuesSliders() {
        upperMassSlider.setValue(initMass1);
        lowerMassSlider.setValue(initMass2);
        upperPendulumLengthSlider.setValue(initUpperArmLength);
        lowerPendulumLengthSlider.setValue(initLowerArmLength);
        setInitValueAngleSliders();
        frictionSlider.setValue(propertiesParser.parsedDoubleProp("init_friction", 0.001));
        maxTraceLengthSlider.setValue(INIT_MAX_TRACE_LENGTH);
        cameraFieldOfViewSlider.setValue(INIT_CAMERA_FOV);
        cameraMaxVelocitySlider.setValue(INIT_MAX_CAMERA_VELOCITY);
    }

    private void setInitValueAngleSliders() {
        angle1Slider.setMin(-Math.PI);
        angle1Slider.setValue(Math.toRadians(initAngle1));
        angle1Slider.setMax(Math.PI);
        angle2Slider.setMin(-Math.PI);
        angle2Slider.setValue(Math.toRadians(initAngle2));
        angle2Slider.setMax(Math.PI);
        vAngular1Slider.setMin(-MAX_ANGULAR_VELOCITY);
        vAngular1Slider.setMax(MAX_ANGULAR_VELOCITY);
        vAngular2Slider.setMin(-MAX_ANGULAR_VELOCITY);
        vAngular2Slider.setMax(MAX_ANGULAR_VELOCITY);
    }

    private void pauseSimButtonAction(boolean isSelected) {
        if (isSelected) {
            animationService.pauseAnimationTimeline();
        } else {
            animationService.startAnimationTimeline();
        }
    }

    void togglePauseButton() {
        pauseButton.setSelected(!pauseButton.isSelected());
    }

    @FXML
    void resetButtonAction() {
        setInitValuesSliders();
        setParamsDoublePendulums();
        doublePendulums.forEach(DoublePendulum::resetVAndAAngular);
    }

    private void setParamsDoublePendulums() {
        var counter = 0;
        for (var doublePendulum : doublePendulums) {
            setDoublePendulumParams(doublePendulum, counter * deltaAngle1);
            counter++;
        }
    }

    @FXML
    private void clearTraceButtonAction() {
        doublePendulums.forEach(DoublePendulum::clearTrace);
    }

    @SuppressWarnings("EmptyMethod")
    @FXML
    private void followDrawSpotButtonAction() {
        //this method should enable/disable follow draw spot
    }

    private Translate initCameraTranslation() {
        return new Translate(0.0, 0.0, INIT_CAMERA_Z_PENDULUMS + selectedDoublePendulum.get().getTranslateZ());
    }

    @FXML
    public void recenterButtonAction() {
        cameraPlatform.setTranslate(initCameraTranslation());
        cameraPlatform.getCamera().setFieldOfView(INIT_CAMERA_FOV);
        subSceneRootMouseControlService.setOrientation(initWorldToCameraAngleX, initWorldToCameraAngleY);
        subSceneRootMouseControlService.setTargetTranslation(Point3D.ZERO);
    }

    @FXML
    private void showAbout() {
        sceneManager.setupScene(ABOUT_SCENE);
    }

    public AnchorPane getAnimationPane() {
        return animationPane;
    }
}
