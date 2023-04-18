package hzt.utils

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import kotlin.test.assertEquals

class IntegratorKtTest {

    @Test
    // TODO: 4/18/2023 implement damped spring system with exact solution and approximation by runge kutta 4
    fun `approximation by Runge kutta 4 should approach exact solution`() {
        val (a, b, c, d) = doubleArrayOf(1.0, 2.0, 3.0, 4.0).rungeKutta4(0.1) { (_, b, c, d) ->
            doubleArrayOf(b, c, d, 0.0)
        }
        assertAll(
            { assertEquals(1.215, a, 1e3) },
            { assertEquals(2.0, b, 1e3) },
            { assertEquals(3.0, c, 1e3) },
            { assertEquals(4.0, d, 1e3) }
        )
    }
}
