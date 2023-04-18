package hzt.utils

import java.util.function.Consumer

class Integrator<T>(private val name: String, val integrate: Consumer<T>) {

    override fun toString(): String = name
}

fun DoubleArray.rungeKutta4(h: Double, nextDeltaByOdeSystem: (DoubleArray) -> DoubleArray): DoubleArray {
    val k1 = nextDeltaByOdeSystem(this)
    val k2 = nextDeltaByOdeSystem(DoubleArray(size) { this[it] + k1[it] * (h / 2) })
    val k3 = nextDeltaByOdeSystem(DoubleArray(size) { this[it] + k2[it] * (h / 2) })
    val k4 = nextDeltaByOdeSystem(DoubleArray(size) { this[it] + k3[it] * h })
    return DoubleArray(size) { this[it] + (k1[it] + 2 * k2[it] + 2 * k3[it] + k4[it]) * h / 6 }
}

/**
 * @param prev previous value
 * @param delta current prevValue/h
 * @param h stepSize
 * @return the next vector
 */
fun eulerIntegration(prev: Double, delta: Double, h: Double): Double = prev + delta * h
