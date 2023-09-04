import hzt.utils.queryAs
import io.kotest.matchers.shouldBe
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
internal class TestFxExampleTest {
    /**
     * Will be called with `@Before` semantics, i. e. before each test method.
     *
     * @param stage - Will be injected by the test runner.
     */
    @Start
    private fun start(stage: Stage) {
        val button = Button("click me!").apply {
            id = "myButton"
            onAction = EventHandler { text = "clicked!" }
        }
        stage.setScene(Scene(StackPane(button), 100.0, 100.0))
        stage.show()
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    fun `should contain button with text`(robot: FxRobot) {
        // (lookup by css id):
        val buttonByCssIdLookup = robot.lookup("#myButton").queryAs<Button>()
        // or (lookup by css class):
        val buttonByCssClassLookup = robot.lookup(".button").queryAs<Button>()
        // or (query specific type)
        val button = robot.lookup(".button").queryButton()
        assertAll(
            { buttonByCssIdLookup.text shouldBe "click me!" },
            { buttonByCssClassLookup.text shouldBe "click me!" },
            { button.text shouldBe "click me!" }
        )
    }

    /**
     * @param robot - Will be injected by the test runner.
     */
    @Test
    fun `when button is clicked text changes`(robot: FxRobot) {
        // when:
        robot.clickOn("#myButton")
        // then:
        // (lookup by css id):
        val buttonByCssIdLookup = robot.lookup("#myButton").queryAs<Button>()
        // or (lookup by css class):
        val buttonByCssClassLookup = robot.lookup(".button").queryAs<Button>()
        // or (query specific type)
        val button = robot.lookup(".button").queryButton()
        assertAll(
            { buttonByCssIdLookup.text shouldBe "clicked!" },
            { buttonByCssClassLookup.text shouldBe "clicked!" },
            { button.text shouldBe "clicked!" }
        )
    }
}
