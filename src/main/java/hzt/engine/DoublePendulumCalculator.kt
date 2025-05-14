package hzt.engine

import hzt.utils.Integrator
import hzt.utils.eulerIntegration
import hzt.utils.get
import hzt.utils.rungeKutta4
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class DoublePendulumCalculator(
    private val h: Double = 1.0,
    private val g: Double = 9.81
) {

    internal val integrator: ObjectProperty<Integrator<DoublePendulumCalculator>> = SimpleObjectProperty()
    internal val upperMassXProperty: DoubleProperty = SimpleDoubleProperty()
    internal val upperMassYProperty: DoubleProperty = SimpleDoubleProperty()
    internal val lowerMassXProperty: DoubleProperty = SimpleDoubleProperty()
    internal val lowerMassYProperty: DoubleProperty = SimpleDoubleProperty()
    @JvmField
    internal val upperMassProperty: DoubleProperty = SimpleDoubleProperty()
    @JvmField
    internal val lowerMassProperty: DoubleProperty = SimpleDoubleProperty()
    internal val upperArmLengthProperty: DoubleProperty = SimpleDoubleProperty()
    internal val lowerArmLengthProperty: DoubleProperty = SimpleDoubleProperty()
    internal val angle1Property: DoubleProperty = SimpleDoubleProperty()
    internal val angle2Property: DoubleProperty = SimpleDoubleProperty()
    internal val vAngular1Property: DoubleProperty = SimpleDoubleProperty()
    internal val vAngular2Property: DoubleProperty = SimpleDoubleProperty(Math.toRadians(10.0))
    internal val aAngular1Property: DoubleProperty = SimpleDoubleProperty()
    internal val aAngular2Property: DoubleProperty = SimpleDoubleProperty()
    internal val friction: DoubleProperty = SimpleDoubleProperty()

    val upperMassX get(): Double = upperMassXProperty.get()
    val upperMassY get(): Double = upperMassYProperty.get()
    val lowerMassX get(): Double = lowerMassXProperty.get()
    val lowerMassY get(): Double = lowerMassYProperty.get()
    val upperMass get(): Double = upperMassProperty.get()
    val lowerMass get(): Double = lowerMassProperty.get()

    fun integrateByEuler() {
        val ang1 = angle1Property.get()
        val ang2 = angle2Property.get()
        val vAng1 = vAngular1Property.get()
        val vAng2 = vAngular2Property.get()

        val aAng1 = calculateAngularAcc1(ang1, ang2, vAng1, vAng2)
        val aAng2 = calculateAngularAcc2(ang1, ang2, vAng1, vAng2)
        val vAng1Next = eulerIntegration(vAng1, aAng1, h)
        val vAng2Next = eulerIntegration(vAng2, aAng2, h)
        val angle1Next = eulerIntegration(ang1, vAng1Next, h)
        val angle2Next = eulerIntegration(ang2, vAng2Next, h)

        aAngular1Property.set(aAng1)
        aAngular2Property.set(aAng2)
        vAngular1Property.set(vAng1Next)
        vAngular2Property.set(vAng2Next)
        angle1Property.set(angle1Next)
        angle2Property.set(angle2Next)
    }

    fun integrateByRungeKutta4() {
        Double[
            angle1Property.get(),
            angle2Property.get(),
            vAngular1Property.get(),
            vAngular2Property.get(),
        ]
            .rungeKutta4(h) { (ang1, ang2, vAng1, vAng2) ->
                val aAng1 = calculateAngularAcc1(ang1, ang2, vAng1, vAng2)
                val aAng2 = calculateAngularAcc2(ang1, ang2, vAng1, vAng2)
                aAngular1Property.set(aAng1)
                aAngular2Property.set(aAng2)
                Double[
                    vAng1, // dangle1/dt = vAngular1
                    vAng2, // dangle2/dt = vAngular2
                    aAng1, // dvAngular1/dt = aAngular1
                    aAng2, // dvAngular2/dt = aAngular2
                    0.0, // daAngular1/dt = 0
                    0.0  // daAngular2/dt = 0
                ]
            }.let { (angle1, angle2, vAngular1, vAngular2) ->
                this.angle1Property.set(angle1)
                this.angle2Property.set(angle2)
                this.vAngular1Property.set(vAngular1)
                this.vAngular2Property.set(vAngular2)
            }
    }

    fun updatePendulumPosition() {
        val upperArmLength = upperArmLengthProperty.get()
        val lowerArmLength = lowerArmLengthProperty.get()
        val angle1 = angle1Property.get()
        val angle2 = angle2Property.get()
        val upperMassX = sin(angle1) * upperArmLength
        val upperMassY = cos(angle1) * upperArmLength
        this.upperMassXProperty.set(upperMassX)
        this.upperMassYProperty.set(upperMassY)
        lowerMassXProperty.set(upperMassX + (sin(angle2) * lowerArmLength))
        lowerMassYProperty.set(upperMassY + (cos(angle2) * lowerArmLength))
    }

    private fun calculateAngularAcc1(ang1: Double, ang2: Double, vAng1: Double, vAng2: Double): Double {
        val num1 = -g * (2.0 * upperMassProperty.get() + lowerMassProperty.get()) * sin(ang1)
        val num2 = -lowerMassProperty.get() * g * sin(ang1 - 2.0 * ang2)
        val num3 = -2.0 * sin(ang1 - ang2) * lowerMassProperty.get()
        val upperArmLength = upperArmLengthProperty.get()
        val num4 = vAng2 * vAng2 * lowerArmLengthProperty.get() + vAng1 * vAng1 * upperArmLength * cos(ang1 - ang2)
        val den = upperArmLength * (2.0 * upperMassProperty.get() * lowerMassProperty.get() - lowerMassProperty.get() * cos(2.0 * ang1 - 2.0 * ang2))
        return (num1 + num2 + num3 * num4) / den
    }

    private fun calculateAngularAcc2(ang1: Double, ang2: Double, vAng1: Double, vAng2: Double): Double {
        val num1 = 2.0 * sin(ang1 - ang2)
        val upperMass = upperMassProperty.get()
        val lowerMass = lowerMassProperty.get()
        val num2 = vAng1 * vAng1 * upperArmLengthProperty.get() * (upperMass + lowerMass)
        val num3 = g * (upperMass + lowerMass) * cos(ang1)
        val lowerArmLength = lowerArmLengthProperty.get()
        val num4 = vAng2 * vAng2 * lowerArmLength * lowerMass * cos(ang1 - ang2)
        val den = lowerArmLength * (2.0 * upperMass * lowerMass - lowerMass * cos(2.0 * ang1 - 2.0 * ang2))
        return num1 * (num2 + num3 + num4) / den
    }

    fun update() {
        integrator.get().integrate(this)
        setBoundToAngles()
        updatePendulumPosition()
        simulateFriction()
    }

    private fun setBoundToAngles() {
        if (angle1Property.get() > PI) angle1Property.set(angle1Property.get() - 2.0 * PI)
        if (angle1Property.get() < -PI) angle1Property.set(angle1Property.get() + 2.0 * PI)
        if (angle2Property.get() > PI) angle2Property.set(angle2Property.get() - 2.0 * PI)
        if (angle2Property.get() < -PI) angle2Property.set(angle2Property.get() + 2.0 * PI)
    }

    private fun simulateFriction() {
        vAngular1Property.set(vAngular1Property.get() * (1.0 - friction.get()))
        vAngular2Property.set(vAngular2Property.get() * (1.0 - friction.get()))
    }

    fun resetVAndAAngular() {
        vAngular1Property.set(0.0)
        vAngular2Property.set(0.0)
        aAngular1Property.set(0.0)
        aAngular2Property.set(0.0)
    }

    fun copyState(calculator: DoublePendulumCalculator, deltaAngle1: Double = 0.0) {
        angle1Property.set(calculator.angle1Property.get() + deltaAngle1)
        angle2Property.set(calculator.angle2Property.get())
        vAngular1Property.set(calculator.vAngular1Property.get())
        vAngular2Property.set(calculator.vAngular2Property.get())
        aAngular1Property.set(calculator.aAngular1Property.get())
        aAngular2Property.set(calculator.aAngular2Property.get())
    }
}