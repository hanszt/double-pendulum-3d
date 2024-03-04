package hzt.controller;

import hzt.controller.scene.AboutSceneController;
import hzt.controller.scene.MainSceneController;
import hzt.controller.scene.SceneController;
import hzt.model.PropertiesParser;
import hzt.service.StandardAboutService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Clock;
import java.util.EnumMap;
import java.util.Map;

import static hzt.controller.SceneManager.Scene.ABOUT_SCENE;

public final class SceneManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(SceneManager.class);

    private final Map<Scene, SceneController> sceneControllerMap = new EnumMap<>(Scene.class);
    private final ObjectProperty<SceneController> curSceneController = new SimpleObjectProperty<>();

    private final Stage stage;
    private final PropertiesParser propertiesParser;
    private final Clock clock;

    SceneManager(Stage stage, PropertiesParser propertiesParser, Clock clock) {
        this.clock = clock;
        this.propertiesParser = propertiesParser;
        this.stage = stage;
        loadFrontend();
    }

    private void loadFrontend() {
        try {
            sceneControllerMap.put(Scene.MAIN_SCENE, new MainSceneController(this, propertiesParser, clock));
            sceneControllerMap.put(ABOUT_SCENE, new AboutSceneController(this, new StandardAboutService(), propertiesParser, clock));
            curSceneController.set(sceneControllerMap.get(Scene.MAIN_SCENE));
        } catch (IOException e) {
            LOGGER.error("Something went wrong when loading fxml frontend...", e);
        }
    }

    public enum Scene {

        MAIN_SCENE("mainScene.fxml"),
        ABOUT_SCENE("aboutScene.fxml");

        private final String fxmlFileName;

        Scene(String fxmlFileName) {
            this.fxmlFileName = fxmlFileName;
        }

        public String formattedName() {
            return name().charAt(0) + name().substring(1).toLowerCase().replace("_", " ");
        }

        public String getFxmlFileName() {
            return fxmlFileName;
        }
    }

    public void setupScene(Scene scene) {
        final var sceneController = sceneControllerMap.get(scene);
        curSceneController.set(sceneController);
        stage.setScene(sceneController.getScene());
        sceneController.setupOnce(scene::formattedName);
    }

    public Stage getStage() {
        return stage;
    }

    SceneController getCurSceneController() {
        return curSceneController.get();
    }

    public Map<Scene, SceneController> getSceneControllerMap() {
        return Map.copyOf(sceneControllerMap);
    }
}
