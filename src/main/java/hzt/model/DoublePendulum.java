package hzt.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

import static hzt.model.AppConstants.G;
import static java.lang.Math.*;
import static java.lang.Math.PI;

public class DoublePendulum extends Group {

    private final PendulumMass upperMass;
    private final PendulumMass lowerMass;
    private final PhongMaterial armMaterial = new PhongMaterial();
    private final Cylinder upperArm;
    private final Cylinder lowerArm;

    private final ObjectProperty<Color> massColor = new SimpleObjectProperty<>();

    private final DoubleProperty angle1 = new SimpleDoubleProperty();
    private final DoubleProperty angle2 = new SimpleDoubleProperty();
    private final DoubleProperty vAngular1 = new SimpleDoubleProperty();
    private final DoubleProperty vAngular2 = new SimpleDoubleProperty();
    private final DoubleProperty aAngular1 = new SimpleDoubleProperty();
    private final DoubleProperty aAngular2 = new SimpleDoubleProperty();

    private final DoubleProperty friction = new SimpleDoubleProperty();

    public DoublePendulum() {
        Cylinder anchor = new Cylinder();
        this.upperMass = new PendulumMass();
        this.lowerMass = new PendulumMass();
        this.upperArm = new Cylinder();
        this.lowerArm = new Cylinder();
        this.getChildren().addAll(anchor, upperArm, lowerArm);
        upperMass.addWeightToFxGroup(this);
        lowerMass.addWeightToFxGroup(this);
        configureComponents();
    }

    private void configureComponents() {
        upperArm.setMaterial(armMaterial);
        lowerArm.setMaterial(armMaterial);
        bindColorProperties();
    }

    private void bindColorProperties() {
        upperMass.diffuseColorProperty().bind(massColor);
        lowerMass.diffuseColorProperty().bind(massColor);
    }

    public void update() {
        calculateAngularAccM1();
        calculateAngularAccM2();
        calculateAngleVars();
        setBoundToAngles();
        updateCoordinates();
        simulateFriction();
    }

    private void updateCoordinates() {
        upperMass.setPosition(cos(angle1.get()) * upperArm.getHeight(), sin(angle1.get()) * upperArm.getHeight());
        Point2D temp = new Point2D(cos(angle2.get()) * lowerArm.getHeight(), sin(angle2.get()) * lowerArm.getHeight());
        Point2D positionUpperMass = upperMass.getPositionXY();
        lowerMass.setPosition(positionUpperMass.getX() + temp.getX(), positionUpperMass.getY() + temp.getY());
    }

    private void calculateAngularAccM1() {
        double num1 = -G * (2 * upperMass.getMass() + lowerMass.getMass()) * sin(angle1.get());
        double num2 = -lowerMass.getMass() * G * sin(angle1.get() - 2 * angle2.get());
        double num3 = -2 * sin(angle1.get() - angle2.get()) * lowerMass.getMass();
        double num4 = vAngular2.get() * vAngular2.get() * lowerArm.getHeight() + vAngular1.get() * vAngular1.get() * upperArm.getHeight() * cos(angle1.get() - angle2.get());
        double den = upperArm.getHeight() * (2 * upperMass.getMass() * lowerMass.getMass() - lowerMass.getMass() * cos(2 * angle1.get() - 2 * angle2.get()));
        aAngular1.set(num1 + num2 + num3 * num4 / den);
    }

    private void calculateAngularAccM2() {
        double num1 = 2 * sin(angle1.get() - angle2.get());
        double num2 = vAngular1.get() * vAngular1.get() * upperArm.getHeight() * (upperMass.getMass() + lowerMass.getMass());
        double num3 = G * (upperMass.getMass() + lowerMass.getMass()) * cos(angle1.get());
        double num4 = vAngular2.get() * vAngular2.get() * lowerArm.getHeight() * lowerMass.getMass() * cos(angle1.get() - angle2.get());
        double den = lowerArm.getHeight() * (2 * upperMass.getMass() * lowerMass.getMass() - lowerMass.getMass() * cos(2 * angle1.get() - 2 * angle2.get()));
        aAngular2.set(num1 * (num2 + num3 + num4) / den);
    }

    private void calculateAngleVars() {
        vAngular1.set(vAngular1.get() + aAngular1.get());
        vAngular2.set(vAngular2.get() + aAngular2.get());
        angle1.set(angle1.get() + vAngular1.get());
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
    }

    public DoubleProperty upperArmLengthProperty() {
        return upperArm.heightProperty();
    }

    public DoubleProperty lowerArmLengthProperty() {
        return lowerArm.heightProperty();
    }

    public ObjectProperty<Color> massColorProperty() {
        return massColor;
    }

    public ObjectProperty<Color> armColorProperty() {
        return armMaterial.diffuseColorProperty();
    }
}
