package hzt.model;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

public abstract sealed class CoordinateSystem3D extends Group permits CylinderCoordinateSystem3D {

    static Material getMaterial(Color diffuseColor, Color specularColor) {
        var material = new PhongMaterial();
        material.setDiffuseColor(diffuseColor);
        material.setSpecularColor(specularColor);
        return material;
    }

    abstract Shape3D buildAxis(Material material, Point3D rotationAxis);
}
