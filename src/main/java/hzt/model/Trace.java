package hzt.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Shape3D;

public class Trace extends Group {

    private final PhongMaterial material = new PhongMaterial();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    public Trace() {
        material.diffuseColorProperty().bind(color);
    }

    public int size() {
        return getChildren().size();
    }

    public void add(Shape3D shape3D) {
        shape3D.setMaterial(material);
        getChildren().add(shape3D);
    }

    public void removeFirst() {
        getChildren().remove(0);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void clear() {
        getChildren().clear();
    }
}
