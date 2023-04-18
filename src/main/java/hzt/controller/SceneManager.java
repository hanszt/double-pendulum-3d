package hzt.controller;

import hzt.controller.scene.AboutSceneController;
import hzt.controller.scene.MainSceneController;
import hzt.controller.scene.SceneController;
import hzt.service.AboutService;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static hzt.model.AppConstants.Scene;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;

public class SceneManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SceneManager.class);

    private final Stage stage;
    private final Map<Scene, SceneController> sceneControllerMap = new EnumMap<>(Scene.class);
    private SceneController curSceneController = null;

    SceneManager(Stage stage) {
        this.stage = stage;
        loadFrontend();
    }

    private void loadFrontend() {
        try {
            sceneControllerMap.put(MAIN_SCENE, new MainSceneController(this));
            sceneControllerMap.put(ABOUT_SCENE, new AboutSceneController(this, new AboutService()));
            curSceneController = sceneControllerMap.get(MAIN_SCENE);
        } catch (IOException e) {
            LOGGER.error("Something went wrong when loading fxml frontend...", e);
        }
    }

    public void setupScene(Scene scene) {
        curSceneController = sceneControllerMap.get(scene);
        stage.setScene(curSceneController.getScene());
        if (!curSceneController.isSetup()) {
            LOGGER.atInfo().setMessage("setting up {}...").addArgument(scene::formattedName).log();
            curSceneController.setup();
        }
    }

    public Stage getStage() {
        return stage;
    }

    SceneController getCurSceneController() {
        return curSceneController;
    }

    public Map<Scene, SceneController> getSceneControllerMap() {
        return Map.copyOf(sceneControllerMap);
    }
}
