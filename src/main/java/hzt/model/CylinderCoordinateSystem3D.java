package hzt.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;

public final class CylinderCoordinateSystem3D extends CoordinateSystem3D {

    private static final double AXIS_LENGTH = 500.0;
    private static final double AXIS_RADIUS = 1.0;

    public CylinderCoordinateSystem3D() {
        var redMaterial = getMaterial(Color.DARKRED, Color.RED);
        var greenMaterial = getMaterial(Color.DARKGREEN, Color.GREEN);
        var blueMaterial = getMaterial(Color.DARKBLUE, Color.BLUE);
        var xAxis = buildAxis(redMaterial, Rotate.Z_AXIS);
        var yAxis = buildAxis(greenMaterial, Rotate.Y_AXIS);
        var zAxis = buildAxis(blueMaterial, Rotate.X_AXIS);
        getChildren().addAll(xAxis, yAxis, zAxis);
    }

    Shape3D buildAxis(Material material, Point3D rotationAxis) {
        var axis = new Cylinder(AXIS_RADIUS, AXIS_LENGTH);
        axis.setRotationAxis(rotationAxis);
        axis.setRotate(90.0);
        axis.setMaterial(material);
        return axis;
    }

}
