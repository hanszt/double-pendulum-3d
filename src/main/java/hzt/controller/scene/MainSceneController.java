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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.LocalTime;

import static hzt.model.AppConstants.*;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;

public class MainSceneController extends SceneController {

    private static final Logger LOGGER = LogManager.getLogger(MainSceneController.class);

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
    private Slider distributionSlider;
    @FXML
    private Slider numberOfObjectsSlider;
    @FXML
    private Slider seedSizeSlider;
    @FXML
    private Slider turnFractionSlider;
    @FXML
    private Slider highlightingSlider;
    @FXML
    private Slider sizeSlider;
    @FXML
    private Slider animationIncrementSlider;
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
        appearanceController.configureColorPickers(
                doublePendulum.massColorProperty(), doublePendulum.armColorProperty());
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
        doublePendulum.printParams();
    }

    private EventHandler<ActionEvent> animationLoopEventHandler() {
        return loop -> animationService.run(doublePendulum, cameraPlatform);
    }

    private void updateStatisticsLoop(ActionEvent loopEvent) {
        Duration runTimeSim = Duration.millis((LocalTime.now().toNanoOfDay() - startTimeScene.toNanoOfDay()) / 1e6);
        statisticsController.updateGroupStatistics(doublePendulum, subSceneRootMouseControlService);
        statisticsController.updateGlobalStatistics(runTimeSim);
        statisticsController.updateCameraPlatformStatistics(cameraPlatform);
    }

    private void configureSliders() {
        turnFractionSlider.valueProperty().addListener((o,c,n) -> updateSim());
        distributionSlider.valueProperty().addListener((o, c, n) -> updateSim());
        sizeSlider.valueProperty().addListener((o, c, n) -> updateSim());
        highlightingSlider.valueProperty().addListener((o, c, n) -> updateSim());
        cameraMaxAccelerationSlider.setDisable(true);
    }

    private void updateSim() {
        doublePendulum.update();
        statisticsController.updateGroupStatistics(doublePendulum, subSceneRootMouseControlService);
    }

    private void setInitControlValues() {
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
    private void updateButtonAction() {
        doublePendulum.update();
    }

    @FXML
    private void goldenRatioButtonAction() {

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
