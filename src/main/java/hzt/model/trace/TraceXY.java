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

public abstract sealed class TraceXY extends Group
        permits BallTrace, RoundLinkTrace2D, SquareLinkTrace2D, TraceXY.NoTrace {

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
        return new NoTrace();
    }

    public static final class NoTrace extends TraceXY {

        @Override
        void updateTrace(double x, double y) {
            //nothing to update...
        }
    }

    public void update(double x, double y) {
        updateTrace(x, y);
        updateTraceLength();
    }

    abstract void updateTrace(double x, double y);

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
        getChildren().removeFirst();
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
