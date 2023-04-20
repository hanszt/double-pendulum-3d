package hzt.controller.scene;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * A singleton list to hold all main scene controllers to be able to reset all instances
 */
final class MainSceneControllers {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainSceneControllers.class);

    private MainSceneControllers() {
    }

    private static class Holder {
        /**
         * Is only instantiated after Holder.mainSceneControllers is called for the first time (Lazy initialization)
         * <p>
         * A weakHashMap is used to enable removal by garbage collector if the controller in the set is not used anymore
         */
        @SuppressWarnings("StaticCollection")
        private static final Set<MainSceneController> mainSceneControllers = Collections.newSetFromMap(new WeakHashMap<>());
    }

    static void configureResetAllInstances(MainSceneController mainSceneController, Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_TYPED, MainSceneControllers::resetAllInstances);
        Holder.mainSceneControllers.add(mainSceneController);
        scene.getWindow().setOnCloseRequest(o -> Holder.mainSceneControllers.remove(mainSceneController));
    }

    private static void resetAllInstances(KeyEvent e) {
        if ("r".equals(e.getCharacter())) {
            LOGGER.info("Resetting all main scene controllers... ({})", Holder.mainSceneControllers.size());
            Holder.mainSceneControllers.forEach(MainSceneController::resetButtonAction);
        }
    }
}
