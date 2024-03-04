package hzt.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public final class CylinderCoordinateSystem3D extends CoordinateSystem3D {

    private static final double AXIS_LENGTH = 500.0;
    private static final double AXIS_RADIUS = 1.0;

    public CylinderCoordinateSystem3D() {
        var xAxis = buildAxis(getMaterial(Color.DARKRED, Color.RED), Rotate.Z_AXIS);
        var yAxis = buildAxis(getMaterial(Color.DARKGREEN, Color.GREEN), Rotate.Y_AXIS);
        var zAxis = buildAxis(getMaterial(Color.DARKBLUE, Color.BLUE), Rotate.X_AXIS);
        getChildren().addAll(xAxis, yAxis, zAxis);
    }

    Cylinder buildAxis(Material material, Point3D rotationAxis) {
        var axis = new Cylinder(AXIS_RADIUS, AXIS_LENGTH);
        axis.setRotationAxis(rotationAxis);
        axis.setRotate(90.0);
        axis.setMaterial(material);
        return axis;
    }

}
