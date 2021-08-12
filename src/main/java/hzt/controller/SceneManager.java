package hzt.controller;

import hzt.controller.scene.AboutSceneController;
import hzt.controller.scene.MainSceneController;
import hzt.controller.scene.SceneController;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static hzt.model.AppConstants.Scene;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;

public class SceneManager {

    private static final Logger LOGGER = LogManager.getLogger(SceneManager.class);

    private final Stage stage;
    private final Map<Scene, SceneController> sceneControllerMap = new EnumMap<>(Scene.class);
    private SceneController curSceneController;

    public SceneManager(Stage stage) {
        this.stage = stage;
        loadFrontend();
    }

    private void loadFrontend() {
        try {
            sceneControllerMap.put(MAIN_SCENE, new MainSceneController(this));
            sceneControllerMap.put(ABOUT_SCENE, new AboutSceneController(this));
            curSceneController = sceneControllerMap.get(MAIN_SCENE);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.fatal("Something went wrong when loading fxml frontend...", e);
        }
    }

    public void setupScene(Scene scene) {
        curSceneController = sceneControllerMap.get(scene);
        stage.setScene(curSceneController.getScene());
        if (!curSceneController.isSetup()) {
            LOGGER.info(() -> "setting up " + scene.formattedName() + "...");
            curSceneController.setup();
        }
    }

    public Stage getStage() {
        return stage;
    }

    public SceneController getCurSceneController() {
        return curSceneController;
    }

    public Map<Scene, SceneController> getSceneControllerMap() {
        return sceneControllerMap;
    }
}
