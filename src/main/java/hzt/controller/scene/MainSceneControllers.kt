package hzt.controller.scene

import java.util.Collections
import java.util.WeakHashMap
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.KeyEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * A singleton list to hold all main scene controllers to be able to reset all instances
 */
internal object MainSceneControllers {
    private val LOGGER: Logger = LoggerFactory.getLogger(MainSceneControllers::class.java)

    fun configureResetAllInstances(mainSceneController: MainSceneController, scene: Scene) {
        scene.addEventFilter(KeyEvent.KEY_TYPED, ::allInstancesKeyResponse)
        Holder.mainSceneControllers.add(mainSceneController)
        scene.window.onCloseRequest = EventHandler { Holder.mainSceneControllers.remove(mainSceneController) }
    }

    private fun allInstancesKeyResponse(e: KeyEvent) {
        if ("xx" == e.character) {
            LOGGER.info("Resetting all main scene controllers... ({})", Holder.mainSceneControllers.size)
            Holder.mainSceneControllers.forEach(MainSceneController::resetButtonAction)
        }
        if ("p" == e.character) {
            LOGGER.info("Toggling pause button for all instances... ({})", Holder.mainSceneControllers.size)
            Holder.mainSceneControllers.forEach(MainSceneController::togglePauseButton)
        }
    }

    private object Holder {
        /**
         * Is only instantiated after Holder.mainSceneControllers is called for the first time (Lazy initialization)
         *
         *
         * A weakHashMap is used to enable removal by garbage collector if the controller in the set is not used anymore
         */
        val mainSceneControllers: MutableSet<MainSceneController> = Collections.newSetFromMap(WeakHashMap())
    }
}
