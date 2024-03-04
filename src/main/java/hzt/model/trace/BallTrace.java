package hzt.model.trace;

import javafx.scene.shape.Sphere;

public final class BallTrace extends TraceXY {

    @Override
    public void updateTrace(double x, double y) {
        var sphere = new Sphere();
        sphere.setTranslateX(x);
        sphere.setTranslateY(y);
        add(sphere);
    }

}
