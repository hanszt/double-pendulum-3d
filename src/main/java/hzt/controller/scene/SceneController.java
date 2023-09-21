package hzt.controller.scene;

import hzt.controller.AppManager;
import hzt.controller.FXMLController;
import hzt.controller.SceneManager;
import hzt.model.PropertiesParser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalTime;
import java.util.function.Supplier;

public abstract class SceneController extends FXMLController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SceneController.class);

    private boolean uninitialized = true;

    final PropertiesParser propertiesParser;
    final SceneManager sceneManager;
    final Scene scene;
    final LocalTime startTimeScene;
    final Dimension2D initSceneDimension;
    final Clock clock;

    SceneController(String fxmlFileName, PropertiesParser propertiesParser, SceneManager sceneManager, Clock clock) throws IOException {
        super(fxmlFileName);
        this.clock = clock;
        this.propertiesParser = propertiesParser;
        startTimeScene = LocalTime.now(clock);
        this.sceneManager = sceneManager;
        int sceneWidthProp = propertiesParser.parsedIntProp("init_scene_width", 1200);
        int sceneHeightProp = propertiesParser.parsedIntProp("init_scene_height", 800);
        initSceneDimension = new Dimension2D(sceneWidthProp, sceneHeightProp);
        scene = new Scene(getRoot(), initSceneDimension.getWidth(), initSceneDimension.getHeight());
    }

    abstract void setup();

    @FXML
    public void newInstance() {
        new AppManager(new Stage(), propertiesParser, clock).start();
    }

    @FXML
    void quitInstance() {
        sceneManager.getStage().close();
    }

    @FXML
    void exitProgram() {
        Platform.exit();
    }

    public void setupOnce(Supplier<String> nameSupplier) {
        if (uninitialized) {
            uninitialized = false;
            LOGGER.atInfo().setMessage("setting up {}...").addArgument(nameSupplier).log();
            setup();
        }
    }

    public Scene getScene() {
        return scene;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public LocalTime getStartTimeScene() {
        return startTimeScene;
    }
}
