package hzt.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;

public class PendulumMass {

    private final Cylinder cylinder;
    private final PhongMaterial phongMaterial = new PhongMaterial();

    private final DoubleProperty density = new SimpleDoubleProperty();
    private final DoubleProperty mass = new SimpleDoubleProperty();

    public PendulumMass() {
        this(new Cylinder());
    }
    
    public PendulumMass(Cylinder cylinder) {
        this(cylinder, 1);
    }

    public PendulumMass(Cylinder cylinder, double density) {
        this.cylinder = cylinder;
        this.density.set(density);
        configureComponents();
    }

    private void configureComponents() {
        bindWeightToCalculatedByDensityAndVolume();
        cylinder.setMaterial(phongMaterial);
    }

    private void bindWeightToCalculatedByDensityAndVolume() {
        DoubleProperty height = cylinder.heightProperty();
        DoubleProperty radius = cylinder.radiusProperty();
        mass.bind(density.multiply(Math.PI).multiply(radius).multiply(radius).multiply(height));
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

    public double getDensity() {
        return density.get();
    }

    public DoubleProperty densityProperty() {
        return density;
    }

    public double getMass() {
        return mass.get();
    }

    public DoubleProperty massProperty() {
        return mass;
    }

    @Override
    public String toString() {
        return "PendulumMass{" +
                "cylinder=" + cylinder +
                ", phongMaterial=" + phongMaterial +
                ", density=" + density +
                ", mass=" + mass +
                '}';
    }
}
