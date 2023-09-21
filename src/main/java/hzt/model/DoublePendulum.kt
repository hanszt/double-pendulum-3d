package hzt.model

import hzt.model.link.Link
import hzt.model.link.SquareLink
import hzt.model.trace.BallTrace
import hzt.model.trace.RoundLinkTrace2D
import hzt.model.trace.SquareLinkTrace2D
import hzt.model.trace.TraceXY
import hzt.utils.Integrator
import hzt.utils.component6
import hzt.utils.eulerIntegration
import hzt.utils.get
import hzt.utils.invoke
import hzt.utils.rungeKutta4
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Point2D
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Cylinder
import javafx.scene.transform.Rotate
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class DoublePendulum(val name: String) : Group() {
    private val anchor: Cylinder = Cylinder()

    @JvmField
    val upperPendulum: PendulumCylinder

    @JvmField
    val lowerPendulum: PendulumCylinder

    @JvmField
    val armMaterial = PhongMaterial()
    private val upperArm: Link<*>
    private val lowerArm: Link<*>
    private val traceTypeMap: Map<String?, TraceXY> = mapOf(
        BALL_TRACE to BallTrace(),
        ROUND_LINK_TRACE to RoundLinkTrace2D(),
        SQUARE_LINK_TRACE to SquareLinkTrace2D(),
        NO_TRACE to TraceXY.noTrace()
    )
    private val traceType: ObjectProperty<String?> = SimpleObjectProperty()
    private val massColor: ObjectProperty<Color> = SimpleObjectProperty()
    private val integrator: ObjectProperty<Integrator<DoublePendulum>> = SimpleObjectProperty()
    private val angle1: DoubleProperty = SimpleDoubleProperty()
    private val angle2: DoubleProperty = SimpleDoubleProperty()
    private val vAngular1: DoubleProperty = SimpleDoubleProperty()
    private val vAngular2: DoubleProperty = SimpleDoubleProperty(Math.toRadians(10.0))
    private val aAngular1: DoubleProperty = SimpleDoubleProperty()
    private val aAngular2: DoubleProperty = SimpleDoubleProperty()
    private val friction: DoubleProperty = SimpleDoubleProperty()

    init {
        traceTypeMap.values.forEach { it.drawModeProperty().bindBidirectional(anchor.drawModeProperty()) }
        upperPendulum = PendulumCylinder()
        lowerPendulum = PendulumCylinder()
        upperArm = SquareLink()
        lowerArm = SquareLink()
        children.addAll(anchor, upperArm, lowerArm)
        upperPendulum.addWeightToFxGroup(this)
        lowerPendulum.addWeightToFxGroup(this)
        configureComponents()
        traceType.addListener { _, c, n -> traceTypeChanged(c, n) }
    }

    private fun traceTypeChanged(c: String?, n: String?) {
        c?.let { traceTypeMap[it] }?.let { clearAndRemove(it) }
        traceTypeMap[n]?.let { newTrace -> setTranslateZAndAdd(newTrace) }
    }

    private fun clearAndRemove(curTrace: TraceXY?) {
        curTrace?.clear()
        children.remove(curTrace)
    }

    private fun setTranslateZAndAdd(newTrace: TraceXY) {
        newTrace.translateZ = anchor.height
        children.add(newTrace)
    }

    private fun configureComponents() {
        configureArms()
        configureAnchor()
        bindColorProperties()
        updatePendulumPosition()
    }

    private fun configureArms() {
        armMaterial.specularColor = Color.WHITE
        upperArm.defineStartAndEndpoint(anchor, upperPendulum.cylinder)
        lowerArm.defineStartAndEndpoint(upperPendulum.cylinder, lowerPendulum.cylinder)
        upperArm.setMaterial(armMaterial)
        lowerArm.setMaterial(armMaterial)
    }

    private fun configureAnchor() {
        anchor.rotationAxis = Rotate.X_AXIS
        anchor.rotate = 90.0
        anchor.radius = 2.0
        anchor.height = DEFAULT_ANCHOR_WIDTH
        val material = PhongMaterial()
        material.diffuseColorProperty().bind(massColor)
        material.specularColorProperty().bind(massColor)
        anchor.material = material
    }

    private fun bindColorProperties() {
        val upperArmMaterial = upperPendulum.phongMaterial
        val lowerArmMaterial = lowerPendulum.phongMaterial
        upperArmMaterial.diffuseColorProperty().bind(massColor)
        lowerArmMaterial.diffuseColorProperty().bind(massColor)
        upperArmMaterial.specularColorProperty().bind(massColor)
        lowerArmMaterial.specularColorProperty().bind(massColor)
    }

    fun update() {
        integrator.get().integrate(this)
        setBoundToAngles()
        updatePendulumPosition()
        traceTypeMap[traceType.get()]?.update(lowerPendulum.positionXY)
        simulateFriction()
    }

    fun integrateByEuler() {
        val dt = 1.0
        val ang1 = angle1.get()
        val ang2 = angle2.get()
        val vAng1 = vAngular1.get()
        val vAng2 = vAngular2.get()

        val aAng1 = calculateAngularAcc1(ang1, ang2, vAng1, vAng2)
        val aAng2 = calculateAngularAcc2(ang1, ang2, vAng1, vAng2)
        val vAng1Next = eulerIntegration(vAng1, dt, aAng1)
        val vAng2Next = eulerIntegration(vAng2, dt, aAng2)

        aAngular1.set(aAng1)
        aAngular2.set(aAng2)
        vAngular1.set(vAng1Next)
        vAngular2.set(vAng2Next)
        angle1.set(eulerIntegration(ang1, dt, vAng1Next))
        angle2.set(eulerIntegration(ang2, dt, vAng2Next))
    }

    fun integrateByRungeKutta4() {
        val dt = 1.0
        val (angle1, angle2, vAngular1, vAngular2, aAngular1, aAngular2) = Double[
            angle1.get(),
            angle2.get(),
            vAngular1.get(),
            vAngular2.get(),
            aAngular1.get(),
            aAngular2.get()
        ]
            .rungeKutta4(dt) { (ang1, ang2, vAng1, vAng2) ->
                val aAng1 = calculateAngularAcc1(ang1, ang2, vAng1, vAng2)
                val aAng2 = calculateAngularAcc2(ang1, ang2, vAng1, vAng2)
                Double[
                    vAng1, // dangle1/dt = vAngular1
                    vAng2, // dangle2/dt = vAngular2
                    aAng1, // dvAngular1/dt = aAngular1
                    aAng2, // dvAngular2/dt = aAngular2
                    0.0, // daAngular1/dt = 0
                    0.0  // daAngular2/dt = 0
                ]
            }
        this.aAngular1.set(aAngular1)
        this.aAngular2.set(aAngular2)
        this.vAngular1.set(vAngular1)
        this.vAngular2.set(vAngular2)
        this.angle1.set(angle1)
        this.angle2.set(angle2)
    }

    fun updatePendulumPosition() {
        upperPendulum.setPosition(sin(angle1.get()) * upperArm.length, cos(angle1.get()) * upperArm.length)
        val posLowerMassWithRespectToUpperMass =
            Point2D(sin(angle2.get()) * lowerArm.length, cos(angle2.get()) * lowerArm.length)
        val positionUpperMass = upperPendulum.positionXY
        lowerPendulum.setPosition(
            positionUpperMass.x + posLowerMassWithRespectToUpperMass.x,
            positionUpperMass.y + posLowerMassWithRespectToUpperMass.y
        )
    }

    private fun calculateAngularAcc1(ang1: Double, ang2: Double, vAng1: Double, vAng2: Double): Double {
        val num1 = -G * (2.0 * upperPendulum.mass + lowerPendulum.mass) * sin(ang1)
        val num2 = -lowerPendulum.mass * G * sin(ang1 - 2.0 * ang2)
        val num3 = -2.0 * sin(ang1 - ang2) * lowerPendulum.mass
        val num4 = vAng2 * vAng2 * lowerArm.length + vAng1 * vAng1 * upperArm.length * cos(ang1 - ang2)
        val den = upperArm.length * denP2(ang1, ang2)
        return (num1 + num2 + num3 * num4) / den
    }

    private fun calculateAngularAcc2(ang1: Double, ang2: Double, vAng1: Double, vAng2: Double): Double {
        val num1 = 2.0 * sin(ang1 - ang2)
        val num2 = vAng1 * vAng1 * upperArm.length * (upperPendulum.mass + lowerPendulum.mass)
        val num3 = G * (upperPendulum.mass + lowerPendulum.mass) * cos(ang1)
        val num4 = vAng2 * vAng2 * lowerArm.length * lowerPendulum.mass * cos(ang1 - ang2)
        val den = lowerArm.length * denP2(ang1, ang2)
        return num1 * (num2 + num3 + num4) / den
    }

    private fun denP2(ang1: Double, ang2: Double) =
        (2.0 * upperPendulum.mass * lowerPendulum.mass - lowerPendulum.mass * cos(2.0 * ang1 - 2.0 * ang2))

    private fun setBoundToAngles() {
        if (angle1.get() > PI) angle1.set(angle1.get() - 2.0 * PI)
        if (angle1.get() < -PI) angle1.set(angle1.get() + 2.0 * PI)
        if (angle2.get() > PI) angle2.set(angle2.get() - 2.0 * PI)
        if (angle2.get() < -PI) angle2.set(angle2.get() + 2.0 * PI)
    }

    private fun simulateFriction() {
        vAngular1.set(vAngular1.get() * (1.0 - friction.get()))
        vAngular2.set(vAngular2.get() * (1.0 - friction.get()))
    }

    fun resetVAndAAngular() {
        vAngular1.set(0.0)
        vAngular2.set(0.0)
        aAngular1.set(0.0)
        aAngular2.set(0.0)
    }

    fun clearTrace() {
        traceTypeMap.values.forEach(TraceXY::clear)
    }

    fun traceSize(): Int = traceTypeMap[traceType.get()]?.size() ?: 0

    fun integratorProperty() = integrator

    fun upperArmLengthProperty(): DoubleProperty = upperArm.lengthProperty()

    val upperArmLength: Double
        get() = upperArm.length

    fun lowerArmLengthProperty(): DoubleProperty = lowerArm.lengthProperty()

    val lowerArmLength: Double
        get() = lowerArm.length

    fun massColorProperty(): ObjectProperty<Color> = massColor

    val angle1Deg: Double
        get() = Math.toDegrees(angle1.get())

    fun angle1Property(): DoubleProperty = angle1

    val angle2Deg: Double
        get() = Math.toDegrees(angle2.get())

    fun angle2Property(): DoubleProperty = angle2

    val vAngular1deg: Double
        get() = Math.toDegrees(vAngular1.get())

    fun vAngular1Property(): DoubleProperty = vAngular1

    val vAngular2Deg: Double
        get() = Math.toDegrees(vAngular2.get())

    fun vAngular2Property(): DoubleProperty = vAngular2

    val aAngular1Deg: Double
        get() = Math.toDegrees(aAngular1.get())
    val aAngular2Deg: Double
        get() = Math.toDegrees(aAngular2.get())

    fun getFriction(): Double = friction.get()
    fun frictionProperty(): DoubleProperty = friction
    fun traceTypeProperty(): ObjectProperty<String?> = traceType
    fun traceTypes(): Collection<TraceXY> = traceTypeMap.values


    companion object {
        private const val DEFAULT_ANCHOR_WIDTH = 10.0
        const val G = 9.81
        const val BALL_TRACE = "Ball trace"
        const val ROUND_LINK_TRACE = "Round linked trace"
        const val SQUARE_LINK_TRACE = "Square linked trace"
        const val NO_TRACE = "No trace"
    }
}
