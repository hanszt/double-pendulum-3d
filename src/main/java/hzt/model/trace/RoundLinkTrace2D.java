package hzt.model.trace;

import hzt.model.link.RoundLink2D;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Sphere;

public class RoundLinkTrace2D extends TraceXY {

    @Override
    void updateTrace(Point2D pointXY) {
        if (prevPointXY != null) {
            var link = new RoundLink2D();
            var startSphere = new Sphere();
            startSphere.setTranslateX(pointXY.getX());
            startSphere.setTranslateY(pointXY.getY());
            link.defineStartAndEndpoint(prevPointXY, pointXY);
            var group = new Group(link, startSphere);
            add(group);
        }
        prevPointXY = pointXY;
    }

}
