package hzt.model;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.shape.Sphere;

public class LinkTrace extends TraceXY {

    private Point2D prevPointXY;

    @Override
    void updateTrace(Point2D pointXY) {
        if (prevPointXY != null) {
            Link link = new Link();
            Sphere startSphere = new Sphere();
            startSphere.setTranslateX(pointXY.getX());
            startSphere.setTranslateY(pointXY.getY());
            link.defineStartAndEndpoint(prevPointXY, pointXY);
            Group group = new Group(link, startSphere);
            add(group);
        }
        prevPointXY = pointXY;
    }

    @Override
    public void clear() {
        getChildren().clear();
        prevPointXY = null;
    }
}
