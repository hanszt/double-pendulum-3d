package hzt.controller;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import static hzt.model.AppConstants.*;

public class AppManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppManager.class);

    private static int instances = 0;
    private final int instance;
    private final Stage stage;
    private final SceneManager sceneManager;

    public AppManager(Stage stage) {
        this.stage = stage;
        sceneManager = new SceneManager(stage);
        ++instances;
        instance = instances;
    }

    public void start() {
        LOGGER.atInfo().setMessage(this::startingMessage).log();
        sceneManager.setupScene(Scene.MAIN_SCENE);
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
        stage.setMinWidth(MIN_STAGE_DIMENSION.getWidth());
        stage.setMinHeight(MIN_STAGE_DIMENSION.getHeight());
        stage.setOnCloseRequest(e -> LOGGER.atInfo().setMessage(this::closingText).log());
        Optional.ofNullable(getClass().getResourceAsStream("/images/icon.png"))
        .ifPresent(inputStream -> stage.getIcons().add(new Image(inputStream)));
    }

    private String closingText() {
        var startTimeSim = sceneManager.getCurSceneController().getStartTimeScene();
        var stopTimeSim = LocalTime.now();
        var runTimeSim = Duration.millis((stopTimeSim.toNanoOfDay() - startTimeSim.toNanoOfDay()) / 1e6);
        return "%s%nAnimation Runtime of instance %d: %.2f seconds%n%s%n".formatted(CLOSING_MESSAGE,
                instance, runTimeSim.toSeconds(), DOTTED_LINE);
    }

}
