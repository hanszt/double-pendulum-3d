package hzt.model.trace;

import javafx.geometry.Point2D;
import javafx.scene.shape.Sphere;

public final class BallTrace extends TraceXY {

    @Override
    public void updateTrace(Point2D point2D) {
        var sphere = new Sphere();
        sphere.setTranslateX(point2D.getX());
        sphere.setTranslateY(point2D.getY());
        add(sphere);
    }

}
