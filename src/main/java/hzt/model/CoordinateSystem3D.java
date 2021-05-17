package hzt.model;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

public abstract class CoordinateSystem3D extends Group {

    Material getMaterial(Color diffuseColor, Color specularColor) {
        var redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(diffuseColor);
        redMaterial.setSpecularColor(specularColor);
        return redMaterial;
    }

    abstract Shape3D buildAxis(Material material, Point3D rotationAxis);
}
