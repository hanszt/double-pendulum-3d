package hzt.controller.scene;

import hzt.controller.SceneManager;
import hzt.controller.sub_pane.AppearanceController;
import hzt.controller.sub_pane.StatisticsController;
import hzt.model.DoublePendulum;
import hzt.model.MovableCameraPlatform;
import hzt.service.AnimationService;
import hzt.service.IAnimationService;
import hzt.service.IMouseControlService;
import hzt.service.MouseControlService;
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
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalTime;

import static hzt.model.AppConstants.*;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;

public class MainSceneController extends SceneController {

    @FXML
    private Slider vAngular2Slider;
    @FXML
    private Slider vAngular1Slider;

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

    private final Group subSceneRoot = new Group();
    private final Group root = new Group();
    private final DoublePendulum doublePendulum = new DoublePendulum();

    private final IMouseControlService subSceneRootMouseControlService = new MouseControlService(root);
    private final StatisticsController statisticsController = new StatisticsController();
    private final AppearanceController appearanceController = new AppearanceController(this, doublePendulum);

    private final IAnimationService animationService = new AnimationService();

    private final SubScene subScene3D = new SubScene(subSceneRoot, 0, 0, true, SceneAntialiasing.BALANCED);
    private final MovableCameraPlatform cameraPlatform = new MovableCameraPlatform();

    public MainSceneController(SceneManager sceneManager) throws IOException {
        super(MAIN_SCENE.getFxmlFileName(), sceneManager);
        this.animationPane.getChildren().add(subScene3D);

        this.subSceneRoot.getChildren().add(root);
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
        configureSliders();
        configureAppearanceController();
        configureDoublePendulum(doublePendulum);
        subSceneRootMouseControlService.initMouseControl(animationPane);
        subSceneRootMouseControlService.setOrientation(INIT_WORLD_TO_CAMERA_ANGLE_X, INIT_WORLD_TO_CAMERA_ANGLE_Y);
        root.getChildren().addAll(doublePendulum);
        animationService.addLoopHandlersToTimelines(true, animationLoopEventHandler(), this::updateStatisticsLoop);
    }

    private void configureAppearanceController() {
        appearanceController.configureColorPickers();
        appearanceController.configureStageControlButtons(sceneManager.getStage());
    }

    private void configureCameraPlatform(MovableCameraPlatform cameraPlatform) {
        cameraPlatform.setTranslate(INIT_CAMERA_PLATFORM_TRANSLATION);
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

    private void configureAnimationPane(AnchorPane animationPane) {
        animationPane.setPrefSize(INIT_ANIMATION_PANE_DIMENSION.getWidth(), INIT_ANIMATION_PANE_DIMENSION.getHeight());
        animationPane.setBackground(new Background(new BackgroundFill(INIT_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
    }

    private void configureComboBoxes() {

    }

    private void configureDoublePendulum(DoublePendulum doublePendulum) {

    }

    private EventHandler<ActionEvent> animationLoopEventHandler() {
        return loop -> animationService.run(doublePendulum, cameraPlatform);
    }

    private void updateStatisticsLoop(ActionEvent loopEvent) {
        Duration runTimeSim = Duration.millis((LocalTime.now().toNanoOfDay() - startTimeScene.toNanoOfDay()) / 1e6);
        statisticsController.updateGroupStatistics(doublePendulum, (MouseControlService) subSceneRootMouseControlService);
        statisticsController.updateGlobalStatistics(runTimeSim);
        statisticsController.updateCameraPlatformStatistics(cameraPlatform);
    }

    private void configureSliders() {
        doublePendulum.getUpperCylinder().massProperty().bind(upperMassSlider.valueProperty());
        doublePendulum.getLowerCylinder().massProperty().bind(lowerMassSlider.valueProperty());
        doublePendulum.upperArmLengthProperty().bind(upperPendulumLengthSlider.valueProperty());
        doublePendulum.lowerArmLengthProperty().bind(lowerPendulumLengthSlider.valueProperty());
        doublePendulum.angle1Property().bindBidirectional(angle1Slider.valueProperty());
        doublePendulum.angle2Property().bindBidirectional(angle2Slider.valueProperty());
        doublePendulum.vAngular1Property().bindBidirectional(vAngular1Slider.valueProperty());
        doublePendulum.vAngular2Property().bindBidirectional(vAngular2Slider.valueProperty());
        doublePendulum.frictionProperty().bind(frictionSlider.valueProperty());
    }

    private static final double DEFAULT_MASS = 10;
    private static final double DEFAULT_ARM_LENGTH = 50;
    private static final double INIT_FRICTION = .02;
    private static final double MAX_ANGULAR_VELOCITY = Math.PI / 4;
    private static final double INIT_ANGLE2 = 90; //deg

    private void setInitControlValues() {
        upperMassSlider.setValue(DEFAULT_MASS);
        lowerMassSlider.setValue(DEFAULT_MASS);
        upperPendulumLengthSlider.setValue(DEFAULT_ARM_LENGTH);
        lowerPendulumLengthSlider.setValue(DEFAULT_ARM_LENGTH);
        angle1Slider.setMin(-Math.PI);
        angle1Slider.setValue(Math.toRadians(INIT_ANGLE2));
        angle1Slider.setMax(Math.PI);
        angle2Slider.setMin(-Math.PI);
        angle2Slider.setValue(Math.toRadians(INIT_ANGLE2));
        angle2Slider.setMax(Math.PI);
        vAngular1Slider.setMin(-MAX_ANGULAR_VELOCITY);
        vAngular1Slider.setMax(MAX_ANGULAR_VELOCITY);
        vAngular2Slider.setMin(-MAX_ANGULAR_VELOCITY);
        vAngular2Slider.setMax(MAX_ANGULAR_VELOCITY);
        frictionSlider.setValue(INIT_FRICTION);
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
        doublePendulum.reset();
    }

    @FXML
    private void clearTraceButtonAction() {
        doublePendulum.clearTrace();
    }

    @FXML
    private void followDrawSpotButtonAction() {

    }

    @FXML
    public void recenterButtonAction() {
        cameraPlatform.setTranslate(INIT_CAMERA_PLATFORM_TRANSLATION);
        cameraPlatform.getCamera().setFieldOfView(INIT_CAMERA_FOV);
        subSceneRootMouseControlService.setOrientation(INIT_WORLD_TO_CAMERA_ANGLE_X, INIT_WORLD_TO_CAMERA_ANGLE_Y);
        subSceneRootMouseControlService.setTargetTranslation(Point3D.ZERO);
    }

    @FXML
    private void traceModeComboboxAction() {
        //this will execute code when the traceMode combobox is changed by user
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
