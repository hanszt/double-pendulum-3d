package hzt.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import static java.lang.Math.pow;

public class PendulumCylinder {

    private final Cylinder cylinder;
    private final PhongMaterial phongMaterial = new PhongMaterial();

    private final DoubleProperty mass = new SimpleDoubleProperty();

    public PendulumCylinder() {
        this(new Cylinder());
    }

    public PendulumCylinder(Cylinder cylinder) {
        this.cylinder = cylinder;
        configureComponents();
    }

    private static final int DEFAULT_MASS_WIDTH = 10;

    private void configureComponents() {
        bindRadiusToMass();
        cylinder.setHeight(DEFAULT_MASS_WIDTH);
        cylinder.setMaterial(phongMaterial);
        cylinder.setRotationAxis(Rotate.X_AXIS);
        cylinder.setRotate(90);
    }

    private static final double CYLINDER_SIZE_FACTOR = 10;

    private void bindRadiusToMass() {
        mass.addListener((o, c, n) ->
                cylinder.setRadius(pow(n.doubleValue() / CYLINDER_SIZE_FACTOR * cylinder.getHeight(), 1./2)));
    }

    public Point2D getPositionXY() {
        return new Point2D(cylinder.getTranslateX(), cylinder.getTranslateY());
    }

    public void setPosition(double x, double y) {
        cylinder.setTranslateX(x);
        cylinder.setTranslateY(y);
    }

    public void addWeightToFxGroup(Group group) {
        group.getChildren().add(cylinder);
    }

    public ObjectProperty<Color> diffuseColorProperty() {
        return phongMaterial.diffuseColorProperty();
    }

    public Cylinder getCylinder() {
        return cylinder;
    }

    public double getMass() {
        return mass.get();
    }

    public DoubleProperty massProperty() {
        return mass;
    }

    @Override
    public String toString() {
        return "PendulumCylinder{" +
                "cylinder=" + cylinder +
                ", phongMaterial=" + phongMaterial +
                ", mass=" + mass +
                '}';
    }
}
