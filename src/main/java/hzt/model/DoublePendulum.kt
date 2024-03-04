package hzt.model

import hzt.engine.DoublePendulumCalculator
import hzt.model.link.Link
import hzt.model.link.SquareLink
import hzt.model.trace.BallTrace
import hzt.model.trace.RoundLinkTrace2D
import hzt.model.trace.SquareLinkTrace2D
import hzt.model.trace.TraceXY
import javafx.beans.property.DoubleProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Group
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Cylinder
import javafx.scene.transform.Rotate

class DoublePendulum(val name: String) : Group() {
    private val anchor: Cylinder = Cylinder()

    private val upperPendulum: PendulumCylinder
    private val lowerPendulum: PendulumCylinder
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

    val armMaterial = PhongMaterial()
    val calculator: DoublePendulumCalculator

    init {
        traceTypeMap.values.forEach { it.drawModeProperty().bindBidirectional(anchor.drawModeProperty()) }
        upperPendulum = PendulumCylinder()
        lowerPendulum = PendulumCylinder()
        upperArm = SquareLink()
        lowerArm = SquareLink()
        calculator = DoublePendulumCalculator().also {
            upperPendulum.positionXProperty().bind(it.upperMassXProperty)
            upperPendulum.positionYProperty().bind(it.upperMassYProperty)
            upperPendulum.massProperty().bind(it.upperMassProperty)

            lowerPendulum.positionXProperty().bind(it.lowerMassXProperty)
            lowerPendulum.positionYProperty().bind(it.lowerMassYProperty)
            lowerPendulum.massProperty().bind(it.lowerMassProperty)

            upperArm.lengthProperty().bind(it.upperArmLengthProperty)
            lowerArm.lengthProperty().bind(it.lowerArmLengthProperty)
        }
        children.addAll(anchor, upperArm, lowerArm)
        upperPendulum.addWeightToFxGroup(this)
        lowerPendulum.addWeightToFxGroup(this)
        configureComponents()
        traceType.addListener { _, c, n -> traceTypeChanged(c, n) }
    }

    private fun traceTypeChanged(c: String?, n: String?) {
        c?.let { traceTypeMap[it] }?.let(::clearAndRemove)
        traceTypeMap[n]?.let { newTrace -> setTranslateZAndAdd(newTrace) }
    }

    private fun clearAndRemove(curTrace: TraceXY) {
        curTrace.clear()
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
        calculator.updatePendulumPosition()
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
        calculator.update()
        traceTypeMap[traceType.get()]?.update(lowerPendulum.positionX, lowerPendulum.positionY)
    }

    fun clearTrace() {
        traceTypeMap.values.forEach(TraceXY::clear)
    }

    fun traceSize(): Int = traceTypeMap[traceType.get()]?.size() ?: 0

    fun integratorProperty() = calculator.integrator

    fun upperArmLengthProperty(): DoubleProperty = calculator.upperArmLengthProperty

    val upperArmLength: Double
        get() = calculator.upperArmLengthProperty.get()

    fun lowerArmLengthProperty(): DoubleProperty = calculator.lowerArmLengthProperty

    val lowerArmLength: Double
        get() = calculator.lowerArmLengthProperty.get()

    fun massColorProperty(): ObjectProperty<Color> = massColor

    val angle1Deg: Double
        get() = Math.toDegrees(angle1Property().get())

    fun angle1Property(): DoubleProperty = calculator.angle1Property

    val angle2Deg: Double
        get() = Math.toDegrees(angle2Property().get())

    fun angle2Property(): DoubleProperty = calculator.angle2Property

    val vAngular1deg: Double
        get() = Math.toDegrees(vAngular1Property().get())

    fun vAngular1Property(): DoubleProperty = calculator.vAngular1Property

    val vAngular2Deg: Double
        get() = Math.toDegrees(vAngular2Property().get())

    fun vAngular2Property(): DoubleProperty = calculator.vAngular2Property

    val aAngular1Deg: Double
        get() = Math.toDegrees(calculator.aAngular1Property.get())
    val aAngular2Deg: Double
        get() = Math.toDegrees(calculator.aAngular2Property.get())

    fun getFriction(): Double = calculator.friction.get()
    fun frictionProperty(): DoubleProperty = calculator.friction
    fun traceTypeProperty(): ObjectProperty<String?> = traceType
    fun traceTypes(): Collection<TraceXY> = traceTypeMap.values


    companion object {
        private const val DEFAULT_ANCHOR_WIDTH = 10.0
        const val BALL_TRACE = "Ball trace"
        const val ROUND_LINK_TRACE = "Round linked trace"
        const val SQUARE_LINK_TRACE = "Square linked trace"
        const val NO_TRACE = "No trace"
    }
}
