package hzt.model;

import hzt.utils.FxUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

public abstract class TraceXY extends Group {

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

    public void add(Shape3D shape3D) {
        shape3D.setMaterial(material);
        getChildren().add(shape3D);
    }

    public void add(Group group) {
        FxUtils.allShapeDescendents(group).forEach(s -> s.setMaterial(material));
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

    public DoubleProperty maxLengthProperty() {
        return maxLength;
    }
}
