package hzt.engine

import hzt.utils.Integrator
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import javafx.geometry.Point2D
import org.junit.jupiter.api.Test

class DoublePendulumCalculatorTest {

    @Test
    fun testCalculatorFirst100Steps() {
        val doublePendulumCalculatorRungeKutta = DoublePendulumCalculator(h = 0.1).apply {
            upperMassProperty.set(100.0)
            lowerMassProperty.set(100.0)
            upperArmLengthProperty.set(50.0)
            lowerArmLengthProperty.set(30.0)
        }
        val doublePendulumCalculatorEuler = DoublePendulumCalculator(h = 0.1).apply {
            upperMassProperty.set(100.0)
            lowerMassProperty.set(100.0)
            upperArmLengthProperty.set(50.0)
            lowerArmLengthProperty.set(30.0)
        }
        doublePendulumCalculatorRungeKutta.integrator.set(Integrator("Runge Kutta", DoublePendulumCalculator::integrateByRungeKutta4))
        doublePendulumCalculatorEuler.integrator.set(Integrator("Euler", DoublePendulumCalculator::integrateByEuler))

        val rungeKuttaResult = generateSequence {
            doublePendulumCalculatorRungeKutta.update()
            Point2D(doublePendulumCalculatorRungeKutta.lowerMassXProperty.get(), doublePendulumCalculatorRungeKutta.lowerMassYProperty.get())
        }.take(100)
            .toList()
        val eulerResult = generateSequence {
            doublePendulumCalculatorEuler.update()
            Point2D(doublePendulumCalculatorEuler.lowerMassXProperty.get(), doublePendulumCalculatorEuler.lowerMassYProperty.get())
        }.take(100)
            .toList()
        rungeKuttaResult.zip(eulerResult).shouldForAll { (pRunge, pEuler) ->
            pRunge.x shouldBe (pEuler.x plusOrMinus 0.1)
            pRunge.y shouldBe (pEuler.y plusOrMinus 0.1)
        }
    }
}