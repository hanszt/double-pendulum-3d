package hzt.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class Connection extends Cylinder {

    private final Rotate rotateZ = new Rotate();

    private final DoubleProperty startX = new SimpleDoubleProperty();
    private final DoubleProperty startY = new SimpleDoubleProperty();
    private final DoubleProperty startZ = new SimpleDoubleProperty();

    private final DoubleProperty endX = new SimpleDoubleProperty();
    private final DoubleProperty endY = new SimpleDoubleProperty();
    private final DoubleProperty endZ = new SimpleDoubleProperty();

    public Connection() {
        super();
        rotateZ.setAxis(Rotate.Z_AXIS);
        getTransforms().addAll(rotateZ);
        bindPositionBasedOnStartAndEnd();
        bindRotationBasedOnStartAndEnd();
    }

    private void bindRotationBasedOnStartAndEnd() {
        DoubleProperty xDistance = new SimpleDoubleProperty();
        xDistance.bind(startX.subtract(endX));
        DoubleProperty yDistance = new SimpleDoubleProperty();
        yDistance.bind(startY.subtract(endY));
        endX.addListener((o, c, n) -> rotate(xDistance, yDistance));
        endY.addListener((o, c, n) -> rotate(xDistance, yDistance));
    }

    private static final int RIGHT_ANGLE = 90;

    private void rotate(DoubleProperty distance1, DoubleProperty distance2) {
        double angle = Math.toDegrees(Math.atan(distance2.get() / distance1.get()));
        rotateZ.setAngle(angle + RIGHT_ANGLE);
    }

    private void bindPositionBasedOnStartAndEnd() {
        DoubleProperty centerX = centerBasedOnStartAndEnd(startX, endX);
        DoubleProperty centerY = centerBasedOnStartAndEnd(startY, endY);
        DoubleProperty centerZ = centerBasedOnStartAndEnd(startZ, endZ);
        translateXProperty().bind(centerX);
        translateYProperty().bind(centerY);
        translateZProperty().bind(centerZ);
    }

    private DoubleProperty centerBasedOnStartAndEnd(DoubleProperty start, DoubleProperty end) {
        DoubleProperty center = new SimpleDoubleProperty();
        center.bind((start.add(end)).divide(2));
        return center;
    }

    public void defineStartAndEndpoint(Node startNode, Node endNode) {
        startX.bind(startNode.translateXProperty());
        startY.bind(startNode.translateYProperty());
        startZ.bind(startNode.translateZProperty());

        endX.bind(endNode.translateXProperty());
        endY.bind(endNode.translateYProperty());
        endZ.bind(endNode.translateZProperty());
    }

}
