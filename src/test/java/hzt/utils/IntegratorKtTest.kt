package hzt.utils

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class IntegratorKtTest {

    /**
     * [Damped mass-spring system](https://lampx.tugraz.at/~hadley/physikm/apps/odeanalytic/dharmonic.en.php)
     */
    @Test
    // TODO: 4/18/2023 implement damped spring system with exact solution and approximation by runge kutta 4
    fun `approximation by Runge kutta 4 should approach exact solution`() {
        val (a, b, c, d) = doubleArrayOf(1.0, 2.0, 3.0, 4.0).rungeKutta4(0.1) { (_, b, c, d) ->
            doubleArrayOf(b, c, d, 0.0)
        }
        assertSoftly {
            a shouldBe (1.215 plusOrMinus 1e3)
            b shouldBe (2.0 plusOrMinus 1e3)
            c shouldBe (3.0 plusOrMinus 1e3)
            d shouldBe (4.0 plusOrMinus 1e3)
        }
    }
}
