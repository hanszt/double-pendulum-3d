package hzt.model

import hzt.queryAs
import hzt.utils.Integrator
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import javafx.scene.Group
import javafx.scene.PerspectiveCamera
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.testfx.api.FxRobot
import org.testfx.framework.junit5.ApplicationExtension
import org.testfx.framework.junit5.Start
import kotlin.math.PI

@ExtendWith(ApplicationExtension::class)
internal class DoublePendulumTests {

    @Start
    private fun start(stage: Stage) {
        val doublePendulum = DoublePendulum("test").apply {
            id = "pendulum"
            translateX = 200.0
            translateY = 200.0
            upperArmLengthProperty().set(200.0)
            lowerArmLengthProperty().set(200.0)
            angle1Property().set(PI / 4.0)
            angle2Property().set(PI / 2.0)
            armMaterial.apply {
                diffuseColor = Color.BLACK
                specularColor = Color.BLACK
            }
            calculator.upperMassProperty.set(300.0)
            massColorProperty().set(Color.RED)
            integratorProperty().set(Integrator("test") {})
            update()
            rotationAxis = Rotate.Y_AXIS.add(Rotate.X_AXIS)
            rotate = 30.0
        }
        stage.scene = Scene(Group(doublePendulum), 800.0, 800.0).apply {
            camera = PerspectiveCamera().apply { translateZ = 100.0 }
        }
        stage.show()
    }

    @Test
    fun `double pendulum should be constructed properly`(robot: FxRobot) {
        val pendulum = robot.lookup("#pendulum").queryAs<DoublePendulum>()
        pendulum.angle1Property().set(Math.PI / 3.0)
        pendulum.update()

        assertSoftly {
            pendulum.angle2Deg shouldBe (90.0 plusOrMinus 1e-10)
            pendulum.angle1Deg shouldBe (60.0 plusOrMinus 1e-10)
        }
    }
}
