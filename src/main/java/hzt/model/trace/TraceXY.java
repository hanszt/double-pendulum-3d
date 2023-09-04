package hzt.model.trace;

import hzt.utils.FxUtils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;
import org.jetbrains.annotations.Nullable;

public abstract class TraceXY extends Group {

    private final ObjectProperty<DrawMode> drawMode = new SimpleObjectProperty<>();
    private final PhongMaterial material = new PhongMaterial();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final DoubleProperty maxLength = new SimpleDoubleProperty();

    @Nullable
    Point2D prevPointXY;

    TraceXY() {
        material.diffuseColorProperty().bind(color);
        material.specularColorProperty().bind(color);
    }

    public static TraceXY noTrace() {
        return new TraceXY() {
            @Override
            void updateTrace(Point2D pointXY) {
                //nothing to update...
            }
        };
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
        FxUtils.allDescendents(group, Shape3D.class).forEach(s -> {
            s.setMaterial(material);
            s.drawModeProperty().bind(drawMode);
        });
        getChildren().add(group);
    }

    private void updateTraceLength() {
        while (size() > maxLength.get()) {
            removeFirst();
        }
    }

    private void removeFirst() {
        getChildren().remove(0);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    public void clear() {
        getChildren().clear();
        prevPointXY = null;
    }

    public ObjectProperty<DrawMode> drawModeProperty() {
        return drawMode;
    }

    public DoubleProperty maxLengthProperty() {
        return maxLength;
    }
}
