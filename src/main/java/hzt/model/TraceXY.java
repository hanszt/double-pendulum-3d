package hzt.model;

import hzt.utils.FxUtils;
import javafx.beans.property.*;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;

public abstract class TraceXY extends Group {

    private final ObjectProperty<DrawMode> drawMode = new SimpleObjectProperty<>();
    private final PhongMaterial material = new PhongMaterial();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    final DoubleProperty maxLength = new SimpleDoubleProperty();

    protected TraceXY() {
        material.diffuseColorProperty().bind(color);
    }

    public void update(Point2D pointXY) {
        updateTrace(pointXY);
        updateTraceLength();
    }

    abstract void updateTrace(Point2D pointXY);

    public int size() {
        return getChildren().size();
    }

    void add(Shape3D shape3D) {
        shape3D.setMaterial(material);
        shape3D.drawModeProperty().bind(drawMode);
        getChildren().add(shape3D);
    }

    void add(Group group) {
        FxUtils.allShapeDescendents(group).forEach(s -> {
            s.setMaterial(material);
            s.drawModeProperty().bind(drawMode);
        });
        getChildren().add(group);
    }

    private void updateTraceLength() {
        while (size() > maxLength.get()) removeFirst();
    }

    public void removeFirst() {
        getChildren().remove(0);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public abstract void clear();

    public ObjectProperty<DrawMode> drawModeProperty() {
        return drawMode;
    }

    public DoubleProperty maxLengthProperty() {
        return maxLength;
    }
}
