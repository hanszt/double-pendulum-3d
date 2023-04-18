package hzt.model.link;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;

public abstract class Link2D<S extends Shape3D> extends Group {

    private static final int RIGHT_ANGLE = 90;

    private final S shape3D;
    private final Rotate rotateZ = new Rotate();

    private final DoubleProperty startX = new SimpleDoubleProperty();
    private final DoubleProperty startY = new SimpleDoubleProperty();

    private final DoubleProperty endX = new SimpleDoubleProperty();
    private final DoubleProperty endY = new SimpleDoubleProperty();

    Link2D(S shape3D) {
        super(shape3D);
        this.shape3D = shape3D;
        rotateZ.setAxis(Rotate.Z_AXIS);
        getTransforms().addAll(rotateZ);
        bindPositionBasedOnStartAndEnd();
        bindRotationBasedOnStartAndEnd();
    }

    private void bindRotationBasedOnStartAndEnd() {
        DoubleProperty xDistance = bindOperation(startX.subtract(endX));
        DoubleProperty yDistance = bindOperation(startY.subtract(endY));
        DoubleProperty xDistanceSquared = bindOperation(xDistance.multiply(xDistance));
        DoubleProperty yDistanceSquared = bindOperation(yDistance.multiply(yDistance));
        DoubleProperty lengthSquared = bindOperation(xDistanceSquared.add(yDistanceSquared));
        lengthSquared.addListener(this::updateHeightIfNotBound);
        endX.addListener((o, c, n) -> rotate(xDistance, yDistance));
        endY.addListener((o, c, n) -> rotate(xDistance, yDistance));
    }

    void updateHeightIfNotBound(ObservableValue<? extends Number> o, Number c, Number n) {
        if (!heightProperty().isBound()) {
            heightProperty().set(Math.sqrt(n.doubleValue()));
        }
    }

    private void rotate(DoubleProperty distance1, DoubleProperty distance2) {
        double angle = Math.toDegrees(Math.atan(distance2.get() / distance1.get()));
        rotateZ.setAngle(angle + RIGHT_ANGLE);
    }

    private static DoubleProperty bindOperation(DoubleBinding doubleBinding) {
        DoubleProperty doubleProperty = new SimpleDoubleProperty();
        doubleProperty.bind(doubleBinding);
        return doubleProperty;
    }

    private void bindPositionBasedOnStartAndEnd() {
        DoubleProperty centerX = centerBasedOnStartAndEnd(startX, endX);
        DoubleProperty centerY = centerBasedOnStartAndEnd(startY, endY);
        translateXProperty().bind(centerX);
        translateYProperty().bind(centerY);
    }

    private static DoubleProperty centerBasedOnStartAndEnd(DoubleProperty start, DoubleProperty end) {
        DoubleProperty center = new SimpleDoubleProperty();
        center.bind((start.add(end)).divide(2));
        return center;
    }

    public void defineStartAndEndpoint(Node startNode, Node endNode) {
        startX.bind(startNode.translateXProperty());
        startY.bind(startNode.translateYProperty());

        endX.bind(endNode.translateXProperty());
        endY.bind(endNode.translateYProperty());

        translateZProperty().bind(endNode.translateZProperty());
    }

    public void defineStartAndEndpoint(Point2D startPoint, Point2D endPoint) {
        startX.set(startPoint.getX());
        startY.set(startPoint.getY());

        endX.set(endPoint.getX());
        endY.set(endPoint.getY());
    }

    S getShape3D() {
        return shape3D;
    }

    public abstract DoubleProperty heightProperty();

    public double getHeight() {
        return heightProperty().get();
    }

    public void setMaterial(Material material) {
        shape3D.setMaterial(material);
    }
}
