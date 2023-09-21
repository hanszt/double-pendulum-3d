package hzt.controller;

import hzt.model.PropertiesParser;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;


public class AppManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppManager.class);
    private static final String DOTTED_LINE = "----------------------------------------------------------------------------------------\n";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String TITLE = "Double Pendulum 3D";
    private static final String CLOSING_MESSAGE = ANSI_BLUE + "See you next Time! :)" + ANSI_RESET;

    private static int instances = 0;
    private final int instance;
    private final Stage stage;
    private final SceneManager sceneManager;
    private final Dimension2D minStageDimension;
    private final Clock clock;

    public AppManager(Stage stage, PropertiesParser propertiesParser, Clock clock) {
        this.stage = stage;
        this.clock = clock;
        sceneManager = new SceneManager(stage, propertiesParser, clock);
        int sceneWidthProp = propertiesParser.parsedIntProp("init_scene_width", 1200);
        int sceneHeightProp = propertiesParser.parsedIntProp("init_scene_height", 800);
        final Dimension2D initSceneDimension = new Dimension2D(sceneWidthProp, sceneHeightProp);
        minStageDimension = determineMinStageDimension(initSceneDimension);
        ++instances;
        instance = instances;
    }

    private Dimension2D determineMinStageDimension(Dimension2D initSceneDimension) {
        var defaultMinStageWidth = 750;
        var defaultMinStageHeight = 500;
        double minStageWidth = Math.min(initSceneDimension.getWidth(), defaultMinStageWidth);
        double minStageHeight = Math.min(initSceneDimension.getHeight(), defaultMinStageHeight);
        return new Dimension2D(minStageWidth, minStageHeight);
    }

    public void start() {
        LOGGER.atInfo().setMessage(this::startingMessage).log();
        sceneManager.setupScene(SceneManager.Scene.MAIN_SCENE);
        configureStage(stage);
        stage.show();
        LOGGER.info("instance {} started", instance);
    }

    private String startingMessage() {
        return String.format("Starting instance %d of %s at %s...%n",
                instance, TITLE, sceneManager.getCurSceneController()
                        .getStartTimeScene()
                        .format(DateTimeFormatter.ofPattern("hh:mm:ss", Locale.ROOT)));
    }

    private void configureStage(Stage stage) {
        stage.setTitle(String.format("%s (%d)", TITLE, instance));
        stage.setMinWidth(minStageDimension.getWidth());
        stage.setMinHeight(minStageDimension.getHeight());
        stage.onCloseRequestProperty().addListener((o, c, n) -> LOGGER.atInfo().setMessage(this::closingText).log());
        Optional.ofNullable(getClass().getResourceAsStream("/images/icon.png"))
        .ifPresent(inputStream -> stage.getIcons().add(new Image(inputStream)));
    }

    private String closingText() {
        var startTimeSim = sceneManager.getCurSceneController().getStartTimeScene();
        var stopTimeSim = LocalTime.now(clock);
        var runTimeSim = Duration.millis((stopTimeSim.toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        return "%s%nAnimation Runtime of instance %d: %.2f seconds%n%s%n".formatted(CLOSING_MESSAGE,
                instance, runTimeSim.toSeconds(), DOTTED_LINE);
    }

}
