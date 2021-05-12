package hzt.model;

import javafx.geometry.Point2D;
import javafx.scene.shape.Sphere;

public class BallTrace extends TraceXY {

    @Override
    public void updateTrace(Point2D point2D) {
        var sphere = new Sphere();
        sphere.setTranslateX(point2D.getX());
        sphere.setTranslateY(point2D.getY());
        add(sphere);
    }

    @Override
    public void clear() {
        getChildren().clear();
    }
}
