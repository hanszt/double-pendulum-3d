package hzt.model.trace;

import hzt.model.link.SquareLink;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public final class SquareLinkTrace2D extends TraceXY {

    @Override
    void updateTrace(Point2D pointXY) {
        if (prevPointXY != null) {
            var link = new SquareLink();
            var startCylinder = new Cylinder();
            startCylinder.setRotationAxis(Rotate.X_AXIS);
            startCylinder.setRotate(90);
            startCylinder.setTranslateX(pointXY.getX());
            startCylinder.setTranslateY(pointXY.getY());
            link.defineStartAndEndpoint(prevPointXY, pointXY);
            var group = new Group(link, startCylinder);
            add(group);
        }
        prevPointXY = pointXY;
    }

}
