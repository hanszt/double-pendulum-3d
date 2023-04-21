package hzt.controller.scene;

import hzt.controller.AppManager;
import hzt.controller.FXMLController;
import hzt.controller.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalTime;
import java.util.function.Supplier;

import static hzt.model.AppConstants.INIT_SCENE_DIMENSION;

public abstract class SceneController extends FXMLController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SceneController.class);

    private boolean uninitialized = true;

    final SceneManager sceneManager;
    final Scene scene;
    final LocalTime startTimeScene;

    SceneController(String fxmlFileName, SceneManager sceneManager) throws IOException {
        super(fxmlFileName);
        startTimeScene = LocalTime.now();
        this.sceneManager = sceneManager;
        scene = new Scene(getRoot(), INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
    }

    abstract void setup();

    @FXML
    public void newInstance() {
       new AppManager(new Stage()).start();
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
            LOGGER.atInfo().setMessage("setting up {}...").addArgument(nameSupplier).log();
            setup();
            uninitialized = false;
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
