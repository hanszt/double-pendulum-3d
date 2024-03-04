package hzt.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public final class PendulumCylinder {

    private final Cylinder cylinder;
    private final PhongMaterial phongMaterial = new PhongMaterial();
    private final DoubleProperty mass = new SimpleDoubleProperty();

    PendulumCylinder() {
        this(new Cylinder());
    }

    private PendulumCylinder(Cylinder cylinder) {
        this.cylinder = cylinder;
        configureComponents();
    }

    private static final double DEFAULT_MASS_WIDTH = 10.0;

    private void configureComponents() {
        bindRadiusToMass();
        phongMaterial.setDiffuseColor(Color.WHITE);
        cylinder.setHeight(DEFAULT_MASS_WIDTH);
        cylinder.setMaterial(phongMaterial);
        cylinder.setRotationAxis(Rotate.X_AXIS);
        cylinder.setRotate(90.0);
    }

    private static final double CYLINDER_SIZE_FACTOR = 10;

    private void bindRadiusToMass() {
        mass.addListener((o, c, n) ->
                cylinder.setRadius(StrictMath.pow(n.doubleValue() / CYLINDER_SIZE_FACTOR * cylinder.getHeight(), 1.0 / 2.0)));
    }

    public double getPositionX() {
        return positionXProperty().get();
    }

    public double getPositionY() {
        return positionYProperty().get();
    }

    public DoubleProperty positionXProperty() {
        return cylinder.translateXProperty();
    }
    public DoubleProperty positionYProperty() {
        return cylinder.translateYProperty();
    }

    void addWeightToFxGroup(Group group) {
        group.getChildren().add(cylinder);
    }

    PhongMaterial getPhongMaterial() {
        return phongMaterial;
    }

    Cylinder getCylinder() {
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
