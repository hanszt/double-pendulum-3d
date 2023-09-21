package hzt.service;

import hzt.service.inteface.MouseControlService;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;

public class StandardMouseControlService implements MouseControlService {

    private double mouseAnchorX = 0.0;
    private double mouseAnchorY = 0.0;

    private double nodeAnchorTranslateX = 0.0;
    private double nodeAnchorTranslateY = 0.0;

    private double nodeAnchorAngleX = 0.0;
    private double nodeAnchorAngleY = 0.0;

    private final DoubleProperty angleX = new SimpleDoubleProperty();
    private final DoubleProperty angleY = new SimpleDoubleProperty();

    private final Node target;

    public StandardMouseControlService(Node node) {
        target = node;
    }

    public final void initMouseControl(Node node) {
        var xRotate = new Rotate(0.0, Rotate.X_AXIS);
        var yRotate = new Rotate(0.0, Rotate.Y_AXIS);
        target.getTransforms().addAll(xRotate, yRotate);

        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        node.setOnMousePressed(event -> mousePressedEvent(target, event));
        node.setOnMouseDragged(event -> mouseDraggedEvent(target, event));
        node.addEventHandler(ScrollEvent.SCROLL, event -> mouseScrollEvent(target, event));
    }

    private void mousePressedEvent(Node node, MouseEvent event) {
        mouseAnchorX = event.getX();
        mouseAnchorY = event.getY();
        if (event.isPrimaryButtonDown()) {
            nodeAnchorTranslateX = node.getTranslateX();
            nodeAnchorTranslateY = node.getTranslateY();
        } else if (event.isSecondaryButtonDown() || event.isMiddleButtonDown()) {
            nodeAnchorAngleX = angleX.get();
            nodeAnchorAngleY = angleY.get();
        }
    }

    private void mouseDraggedEvent(Node node, MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            double deltaX = mouseAnchorX - event.getX();
            double deltaY = mouseAnchorY - event.getY();
            node.setTranslateX(nodeAnchorTranslateX - deltaX);
            node.setTranslateY(nodeAnchorTranslateY - deltaY);
        } else if (event.isSecondaryButtonDown() || event.isMiddleButtonDown()) {
            angleX.set(nodeAnchorAngleX - (mouseAnchorY - event.getY()));
            angleY.set(nodeAnchorAngleY + (mouseAnchorX - event.getX()));
        }
    }

    private static void mouseScrollEvent(Node node, ScrollEvent event) {
        double delta = event.getDeltaY();
        node.setTranslateZ(node.getTranslateZ() - delta);
    }

    public void setOrientation(double angleX, double angleY) {
        this.angleX.set(angleX);
        this.angleY.set(angleY);
    }

    public void setTargetTranslation(Point3D point3D) {
        target.setTranslateX(point3D.getX());
        target.setTranslateY(point3D.getY());
        target.setTranslateZ(point3D.getZ());
    }

    public double getTargetTranslateX() {
        return target.getTranslateX();
    }

    public double getTargetTranslateY() {
        return target.getTranslateY();
    }

    public double getTargetTranslateZ() {
        return target.getTranslateZ();
    }

    public double getAngleX() {
        return angleX.get();
    }

    public double getAngleY() {
        return angleY.get();
    }

}
