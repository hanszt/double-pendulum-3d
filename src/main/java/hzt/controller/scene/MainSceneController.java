package hzt.controller.scene;

import hzt.controller.SceneManager;
import hzt.controller.sub_pane.AppearanceController;
import hzt.controller.sub_pane.StatisticsController;
import hzt.model.CoordinateSystem3D;
import hzt.model.CylinderCoordinateSystem3D;
import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import hzt.service.AnimationService;
import hzt.service.inteface.IAnimationService;
import hzt.service.inteface.IMouseControlService;
import hzt.service.MouseControlService;
import hzt.utils.FxUtils;
import hzt.utils.StringUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.scene.layout.*;
import javafx.scene.transform.Translate;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static hzt.model.AppConstants.*;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;
import static hzt.model.DoublePendulum.*;

public class MainSceneController extends SceneController {

    private static final double INIT_MASS_1 = parsedDoubleProp("init_mass1", 10);
    private static final double INIT_MASS_2 = parsedDoubleProp("init_mass2", 100);
    private static final double INIT_UPPER_ARM_LENGTH = parsedDoubleProp("init_upper_arm_length", 100);
    private static final double INIT_LOWER_ARM_LENGTH = parsedDoubleProp("init_lower_arm_length", 100);
    private static final double INIT_ANGLE_1 = parsedDoubleProp("init_angle1", 30);
    private static final double INIT_ANGLE_2 = parsedDoubleProp("init_angle2", 30);
    public static final double DELTA_ANGLE_1 = parsedDoubleProp("delta_angle1", 0.1);

    @FXML
    private Tab appearanceTab;
    @FXML
    private Tab statisticsTab;
    @FXML
    private AnchorPane animationPane;
    @FXML
    private ComboBox<String> traceModeCombobox;
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
    private final DoublePendulum frontDoublePendulum = doublePendulums.peek();

    private final CoordinateSystem3D coordinateSystem3D = new CylinderCoordinateSystem3D();

    private final IMouseControlService subSceneRootMouseControlService = new MouseControlService(subSceneRoot);

    private final StatisticsController statisticsController = new StatisticsController();
    private final AppearanceController appearanceController = new AppearanceController(this, doublePendulums);
    private final IAnimationService animationService = new AnimationService();

    private final SubScene subScene3D = new SubScene(subSceneRoot, 0, 0, true, SceneAntialiasing.BALANCED);

    private final MovableCameraPlatform cameraPlatform = new MovableCameraPlatform();

    public MainSceneController(SceneManager sceneManager) throws IOException {
        super(MAIN_SCENE.getFxmlFileName(), sceneManager);
        this.animationPane.getChildren().add(subScene3D);
        this.statisticsTab.setContent(statisticsController.getRoot());
        this.appearanceTab.setContent(appearanceController.getRoot());
    }

    @Override
    public void setup() {
        configureAnimationPane(animationPane);
        configureCameraPlatform(cameraPlatform);
        configureSubScene(subScene3D, animationPane);
        setInitControlValues();
        configureComboBoxes();
        configureAppearanceController();
        configureDoublePendulums();
        configureSelectedDoublePendulum(frontDoublePendulum);
        scene.setOnKeyTyped(this::wireFrameWhenZPressed);
        subSceneRootMouseControlService.initMouseControl(animationPane);
        subSceneRootMouseControlService.setOrientation(INIT_WORLD_TO_CAMERA_ANGLE_X, INIT_WORLD_TO_CAMERA_ANGLE_Y);
        subSceneRoot.getChildren().add(coordinateSystem3D);
        doublePendulums.forEach(doublePendulum ->subSceneRoot.getChildren().add(doublePendulum));
        coordinateSystem3D.visibleProperty().bind(showAxisButton.selectedProperty());
        animationService.addLoopHandlersToTimelines(true, animationLoopEventHandler(), this::updateStatisticsLoop);
    }

    private static final double Z_SPACING = 30;

    private Deque<DoublePendulum> buildDoublePendulums() {
        var amount = parsedIntProp("number_of_pendulums", 1);
        amount = amount > 0 ? amount : 1;
        int lower = -(amount / 2);
        int upper = lower + amount;
        return IntStream.range(lower, upper)
                .mapToObj(this::createDoublePendulum)
                .collect(Collectors.toCollection(ArrayDeque::new));
    }

    private DoublePendulum createDoublePendulum(int index) {
        var doublePendulum = new DoublePendulum();
        doublePendulum.setTranslateZ(index * Z_SPACING);
        return doublePendulum;
    }

    private void configureDoublePendulums() {
        var counter = 0;
        for (DoublePendulum doublePendulum : doublePendulums) {
            configureDoublePendulum(doublePendulum, counter * DELTA_ANGLE_1);
            counter++;
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
        if (e.getCharacter().equals("z")) {
            FxUtils.allShapeDescendents(subSceneRoot).forEach(FxUtils::setDrawMode);
        }
    }

    private void configureAnimationPane(AnchorPane animationPane) {
        animationPane.setPrefSize(INIT_ANIMATION_PANE_DIMENSION.getWidth(), INIT_ANIMATION_PANE_DIMENSION.getHeight());
        animationPane.setBackground(new Background(new BackgroundFill(INIT_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void configureComboBoxes() {
        ObservableList<String> items = traceModeCombobox.getItems();
        items.addAll(BALL_TRACE, ROUND_LINK_TRACE, NO_TRACE, SQUARE_LINK_TRACE);
        String initTraceType = getPROPS().getProperty("init_trace_type", "");
        initTraceType = StringUtils.toOnlyFirstLetterUpperCase(initTraceType);
        initTraceType = items.contains(initTraceType) ? initTraceType : BALL_TRACE;
        traceModeCombobox.setValue(initTraceType);
    }

    private void configureDoublePendulum(DoublePendulum doublePendulum, double deltaAngle) {
        setDoublePendulumParams(doublePendulum, deltaAngle);
        doublePendulum.frictionProperty().bind(frictionSlider.valueProperty());
        doublePendulum.traceTypeProperty().bind(traceModeCombobox.valueProperty());
        doublePendulum.traceTypes().forEach(trace -> trace.maxLengthProperty().bind(maxTraceLengthSlider.valueProperty()));
        doublePendulum.traceTypes().forEach(appearanceController::bindTraceColorToTraceColorPicker);
    }

    private void setDoublePendulumParams(DoublePendulum doublePendulum, double deltaAngle) {
        doublePendulum.reset();
        doublePendulum.getUpperPendulum().massProperty().bind(upperMassSlider.valueProperty());
        doublePendulum.getLowerPendulum().massProperty().bind(lowerMassSlider.valueProperty());
        doublePendulum.upperArmLengthProperty().bind(upperPendulumLengthSlider.valueProperty());
        doublePendulum.lowerArmLengthProperty().bind(lowerPendulumLengthSlider.valueProperty());
        doublePendulum.angle1Property().set(Math.toRadians(INIT_ANGLE_1 + deltaAngle));
        doublePendulum.angle2Property().set(Math.toRadians(INIT_ANGLE_2));
    }

    private void configureSelectedDoublePendulum(DoublePendulum doublePendulum) {
        doublePendulum.angle1Property().bindBidirectional(angle1Slider.valueProperty());
        doublePendulum.angle2Property().bindBidirectional(angle2Slider.valueProperty());
        doublePendulum.vAngular1Property().bindBidirectional(vAngular1Slider.valueProperty());
        doublePendulum.vAngular2Property().bindBidirectional(vAngular2Slider.valueProperty());
    }

    private EventHandler<ActionEvent> animationLoopEventHandler() {
        return loop -> animationService.run(doublePendulums, cameraPlatform);
    }

    private void updateStatisticsLoop(ActionEvent loopEvent) {
        Duration runTimeSim = Duration.between(startTimeScene, LocalTime.now());
        statisticsController.updateGroupStatistics(frontDoublePendulum, subSceneRootMouseControlService);
        statisticsController.updateGlobalStatistics(runTimeSim);
        statisticsController.updateCameraPlatformStatistics(cameraPlatform);
    }

    private static final double MAX_ANGULAR_VELOCITY = Math.PI / 4;
    private static final int INIT_MAX_TRACE_LENGTH = 500;

    private void setInitControlValues() {
        upperMassSlider.setValue(INIT_MASS_1);
        lowerMassSlider.setValue(INIT_MASS_2);
        upperPendulumLengthSlider.setValue(INIT_UPPER_ARM_LENGTH);
        lowerPendulumLengthSlider.setValue(INIT_LOWER_ARM_LENGTH);
        angle1Slider.setMin(-Math.PI);
        angle1Slider.setValue(Math.toRadians(INIT_ANGLE_1));
        angle1Slider.setMax(Math.PI);
        angle2Slider.setMin(-Math.PI);
        angle2Slider.setValue(Math.toRadians(INIT_ANGLE_2));
        angle2Slider.setMax(Math.PI);
        vAngular1Slider.setMin(-MAX_ANGULAR_VELOCITY);
        vAngular1Slider.setMax(MAX_ANGULAR_VELOCITY);
        vAngular2Slider.setMin(-MAX_ANGULAR_VELOCITY);
        vAngular2Slider.setMax(MAX_ANGULAR_VELOCITY);
        frictionSlider.setValue(parsedDoubleProp("init_friction", .001));
        maxTraceLengthSlider.setValue(INIT_MAX_TRACE_LENGTH);
        cameraFieldOfViewSlider.setValue(INIT_CAMERA_FOV);
        cameraMaxVelocitySlider.setValue(INIT_MAX_CAMERA_VELOCITY);
    }

    @FXML
    private void pauseSimButtonAction(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()) {
            animationService.pauseAnimationTimeline();
        } else animationService.startAnimationTimeline();
    }

    @FXML
    private void resetButtonAction() {
        setInitControlValues();
        setParamsDoublePendulumsExclusiveFront();
        doublePendulums.forEach(DoublePendulum::reset);
    }

    private void setParamsDoublePendulumsExclusiveFront() {
        var counter = 0;
        for (var doublePendulum : doublePendulums) {
            if (!doublePendulum.equals(frontDoublePendulum)) {
                setDoublePendulumParams(doublePendulum, counter * DELTA_ANGLE_1);
            }
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
        return new Translate(0, 0, INIT_CAMERA_Z_PENDULUMS + frontDoublePendulum.getTranslateZ());
    }

    @FXML
    public void recenterButtonAction() {
        cameraPlatform.setTranslate(initCameraTranslation());
        cameraPlatform.getCamera().setFieldOfView(INIT_CAMERA_FOV);
        subSceneRootMouseControlService.setOrientation(INIT_WORLD_TO_CAMERA_ANGLE_X, INIT_WORLD_TO_CAMERA_ANGLE_Y);
        subSceneRootMouseControlService.setTargetTranslation(Point3D.ZERO);
    }

    @FXML
    private void showAbout() {
        sceneManager.setupScene(ABOUT_SCENE);
    }

    protected SceneController getBean() {
        return this;
    }

    public AnchorPane getAnimationPane() {
        return animationPane;
    }
}
