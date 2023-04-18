package hzt.utils;

import javafx.scene.Parent;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;

import java.util.ArrayList;
import java.util.List;

public final class FxUtils {

    private FxUtils() {
    }

    public static List<Shape3D> allShapeDescendents(Parent root) {
        List<Shape3D> shape3DList = new ArrayList<>();
        addAllDescendents(root, shape3DList);
        return shape3DList;
    }

    private static void addAllDescendents(Parent parent, List<Shape3D> nodes) {
        for (var node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Shape3D shape3D) {
                nodes.add(shape3D);
            }
            if (node instanceof Parent subParent) {
                addAllDescendents(subParent, nodes);
            }
        }
    }

    public static void setDrawMode(Shape3D shape3D) {
        if (!shape3D.drawModeProperty().isBound()) {
            shape3D.setDrawMode(DrawMode.LINE == shape3D.getDrawMode() ? DrawMode.FILL : DrawMode.LINE);
        }
    }

}
