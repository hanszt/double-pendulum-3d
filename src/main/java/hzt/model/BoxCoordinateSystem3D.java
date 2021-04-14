package hzt.model;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;

public class BoxCoordinateSystem3D extends CoordinateSystem3D {

    public BoxCoordinateSystem3D() {
        PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
        PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        Box xAxis = new Box(500.0, 1, 1);
        xAxis.setMaterial(redMaterial);
        Box yAxis = new Box(1, 500.0, 1);
        yAxis.setMaterial(greenMaterial);
        Box zAxis = new Box(1, 1, 500.0);
        zAxis.setMaterial(blueMaterial);
        getChildren().addAll(xAxis, yAxis, zAxis);
    }
}
