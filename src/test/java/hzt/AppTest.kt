package hzt

import hzt.controller.AppManager
import hzt.model.CashedPropertiesParser
import hzt.model.CylinderCoordinateSystem3D
import hzt.model.DoublePendulum
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import java.nio.file.Path
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import javafx.scene.SubScene
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start

@ExtendWith(ApplicationExtension::class)
class AppTest {

    private val propertiesParser = CashedPropertiesParser.withSource(Path.of("src/test/resources/app-test.properties"))

    @Start
    fun setup(stage: Stage) {
        AppManager(stage, propertiesParser, Clock.fixed(Instant.parse("2023-10-12T12:00:00Z"), ZoneOffset.UTC)).start()
    }

    @Test
    fun testStart(robot: FxRobot) {
        val subScene = robot.lookup<SubScene> { true }.queryAs<SubScene>()
        val nodes = subScene.root.childrenUnmodifiable
        nodes.forEach(::println)
        val nrOfPendulums = propertiesParser.parsedIntProp("init_number_of_pendulums", 0)

        assertSoftly {
            nodes.count { it is DoublePendulum } shouldBe nrOfPendulums
            nodes.count { it is CylinderCoordinateSystem3D } shouldBe 1
        }
    }
}