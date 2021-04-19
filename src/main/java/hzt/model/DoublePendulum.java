package hzt.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import static hzt.model.AppConstants.G;
import static java.lang.Math.*;
import static java.lang.Math.PI;

public class DoublePendulum extends Group {

    private final Cylinder anchor;
    private final PendulumCylinder upperPendulum;
    private final PendulumCylinder lowerPendulum;
    private final PhongMaterial armMaterial = new PhongMaterial();
    private final Link upperArm;
    private final Link lowerArm;

    private final ObjectProperty<TraceType> traceType = new SimpleObjectProperty<>();

    private final ObjectProperty<Color> massColor = new SimpleObjectProperty<>();

    private final DoubleProperty angle1 = new SimpleDoubleProperty();
    private final DoubleProperty angle2 = new SimpleDoubleProperty();
    private final DoubleProperty vAngular1 = new SimpleDoubleProperty();
    private final DoubleProperty vAngular2 = new SimpleDoubleProperty(Math.toRadians(10));
    private final DoubleProperty aAngular1 = new SimpleDoubleProperty();
    private final DoubleProperty aAngular2 = new SimpleDoubleProperty();

    private final DoubleProperty friction = new SimpleDoubleProperty();

    public DoublePendulum() {
        this.anchor = new Cylinder();
        this.upperPendulum = new PendulumCylinder();
        this.lowerPendulum = new PendulumCylinder();
        this.upperArm = new Link();
        this.lowerArm = new Link();
        this.getChildren().addAll(anchor, upperArm, lowerArm);
        upperPendulum.addWeightToFxGroup(this);
        lowerPendulum.addWeightToFxGroup(this);
        configureComponents();
        traceType.addListener(this::traceTypeChanged);
    }

    private void configureComponents() {
        configureArms();
        configureAnchor();
        bindColorProperties();
        updatePendulumPosition();
    }

    private void configureArms() {
        upperArm.defineStartAndEndpoint(anchor, upperPendulum.getCylinder());
        lowerArm.defineStartAndEndpoint(upperPendulum.getCylinder(), lowerPendulum.getCylinder());
        upperArm.setMaterial(armMaterial);
        lowerArm.setMaterial(armMaterial);
    }

    private static final int DEFAULT_ANCHOR_WIDTH = 10;

    private void configureAnchor() {
        anchor.setRotationAxis(Rotate.X_AXIS);
        anchor.setRotate(90);
        anchor.setRadius(2);
        anchor.setHeight(DEFAULT_ANCHOR_WIDTH);
        PhongMaterial material = new PhongMaterial();
        material.diffuseColorProperty().bind(massColor);
        anchor.setMaterial(material);
    }

    private void bindColorProperties() {
        upperPendulum.diffuseColorProperty().bind(massColor);
        lowerPendulum.diffuseColorProperty().bind(massColor);

    }

    public void update() {
        calculateAngularAcc1();
        calculateAngularAcc2();
        calculateAngleVars();
        setBoundToAngles();
        updatePendulumPosition();
        traceType.get().getTraceXY().update(lowerPendulum.getPositionXY());
        simulateFriction();
    }

    private void updatePendulumPosition() {
        upperPendulum.setPosition(sin(angle1.get()) * upperArm.getHeight(), cos(angle1.get()) * upperArm.getHeight());
        Point2D posLowerMassWithRespectToUpperMass =
                new Point2D(sin(angle2.get()) * lowerArm.getHeight(), cos(angle2.get()) * lowerArm.getHeight());
        Point2D positionUpperMass = upperPendulum.getPositionXY();
        lowerPendulum.setPosition(positionUpperMass.getX() + posLowerMassWithRespectToUpperMass.getX(),
                positionUpperMass.getY() + posLowerMassWithRespectToUpperMass.getY());
    }

    private void calculateAngularAcc1() {
        double num1 = -G * (2 * upperPendulum.getMass() + lowerPendulum.getMass()) * sin(angle1.get());
        double num2 = -lowerPendulum.getMass() * G * sin(angle1.get() - 2 * angle2.get());
        double num3 = -2 * sin(angle1.get() - angle2.get()) * lowerPendulum.getMass();
        double num4 = vAngular2.get() * vAngular2.get() * lowerArm.getHeight() + vAngular1.get() * vAngular1.get() *
                upperArm.getHeight() * cos(angle1.get() - angle2.get());
        double den = upperArm.getHeight() * (2 * upperPendulum.getMass() * lowerPendulum.getMass() - lowerPendulum.getMass() *
                cos(2 * angle1.get() - 2 * angle2.get()));
        aAngular1.set((num1 + num2 + num3 * num4) / den);
    }

    private void calculateAngularAcc2() {
        double num1 = 2 * sin(angle1.get() - angle2.get());
        double num2 = vAngular1.get() * vAngular1.get() * upperArm.getHeight() * (upperPendulum.getMass() + lowerPendulum.getMass());
        double num3 = G * (upperPendulum.getMass() + lowerPendulum.getMass()) * cos(angle1.get());
        double num4 = vAngular2.get() * vAngular2.get() * lowerArm.getHeight() * lowerPendulum.getMass() * cos(angle1.get() - angle2.get());
        double den = lowerArm.getHeight() * (2 * upperPendulum.getMass() * lowerPendulum.getMass() - lowerPendulum.getMass() *
                cos(2 * angle1.get() - 2 * angle2.get()));
        aAngular2.set((num1 * (num2 + num3 + num4)) / den);
    }

    private void calculateAngleVars() {
        vAngular1.set(vAngular1.get() + aAngular1.get());
        angle1.set(angle1.get() + vAngular1.get());
        vAngular2.set(vAngular2.get() + aAngular2.get());
        angle2.set(angle2.get() + vAngular2.get());
    }

    private void setBoundToAngles() {
        if (angle1.get() > PI) angle1.set(angle1.get() - 2 * PI);
        if (angle1.get() < -PI) angle1.set(angle1.get() + 2 * PI);
        if (angle2.get() > PI) angle2.set(angle2.get() - 2 * PI);
        if (angle2.get() < -PI) angle2.set(angle2.get() + 2 * PI);
    }

    private void simulateFriction() {
        vAngular1.set(vAngular1.get() * (1 - friction.get()));
        vAngular2.set(vAngular2.get() * (1 - friction.get()));
    }

    public void reset() {
        vAngular1.set(0);
        vAngular2.set(0);
        aAngular1.set(0);
        aAngular2.set(0);
    }

    public void clearTrace() {
        traceType.get().getTraceXY().clear();
    }

    public int traceSize() {
        return traceType.get().traceSize();
    }

    public DoubleProperty upperArmLengthProperty() {
        return upperArm.heightProperty();
    }

    public double getUpperArmLength() {
        return upperArm.getHeight();
    }

    public DoubleProperty lowerArmLengthProperty() {
        return lowerArm.heightProperty();
    }

    public double getLowerArmLength() {
        return lowerArm.getHeight();
    }

    public ObjectProperty<Color> massColorProperty() {
        return massColor;
    }

    public ObjectProperty<Color> armColorProperty() {
        return armMaterial.diffuseColorProperty();
    }

    public PendulumCylinder getUpperPendulum() {
        return upperPendulum;
    }

    public PendulumCylinder getLowerPendulum() {
        return lowerPendulum;
    }

    public double getAngle1Deg() {
        return Math.toDegrees(angle1.get());
    }

    public DoubleProperty angle1Property() {
        return angle1;
    }

    public double getAngle2Deg() {
        return Math.toDegrees(angle2.get());
    }

    public DoubleProperty angle2Property() {
        return angle2;
    }

    public double getVAngular1deg() {
        return Math.toDegrees(vAngular1.get());
    }

    public DoubleProperty vAngular1Property() {
        return vAngular1;
    }

    public double getVAngular2Deg() {
        return Math.toDegrees(vAngular2.get());
    }

    public DoubleProperty vAngular2Property() {
        return vAngular2;
    }

    public double getAAngular1Deg() {
        return Math.toDegrees(aAngular1.get());
    }

    public double getAAngular2Deg() {
        return Math.toDegrees(aAngular2.get());
    }

    public double getFriction() {
        return friction.get();
    }

    public DoubleProperty frictionProperty() {
        return friction;
    }

    public ObjectProperty<TraceType> traceTypeProperty() {
        return traceType;
    }

    private void traceTypeChanged(ObservableValue<? extends TraceType> o, TraceType c, TraceType n) {
        if (c != null) {
            TraceXY curTrace = c.getTraceXY();
            curTrace.clear();
            getChildren().remove(curTrace);
        }
        TraceXY newTrace = n.getTraceXY();
        newTrace.setTranslateZ(anchor.getHeight());

        getChildren().add(newTrace);
    }
}
