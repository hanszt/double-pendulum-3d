package hzt.utils

import java.util.function.Consumer

operator fun DoubleArray.component6() = this[5]
operator fun <T> Consumer<T>.invoke(value: T) = accept(value)
operator fun Double.Companion.get(vararg elements: Double) = doubleArrayOf(*elements)
