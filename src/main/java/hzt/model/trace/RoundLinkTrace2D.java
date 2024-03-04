package hzt.model.trace;

import hzt.model.link.RoundLink;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Sphere;

public final class RoundLinkTrace2D extends TraceXY {

    @Override
    void updateTrace(double x, double y) {
        final var endPoint = new Point2D(x, y);
        if (prevPointXY != null) {
            var link = new RoundLink();
            var startSphere = new Sphere();
            startSphere.setTranslateX(x);
            startSphere.setTranslateY(y);
            link.defineStartAndEndpoint(prevPointXY, endPoint);
            var group = new Group(link, startSphere);
            add(group);
        }
        prevPointXY = endPoint;
    }

}
