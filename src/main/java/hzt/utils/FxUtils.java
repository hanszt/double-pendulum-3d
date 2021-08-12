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
            if (node instanceof Shape3D) {
                nodes.add((Shape3D) node);
            }
            if (node instanceof Parent) {
                addAllDescendents((Parent) node, nodes);
            }
        }
    }

    public static void setDrawMode(Shape3D shape3D) {
        if (!shape3D.drawModeProperty().isBound()) {
            shape3D.setDrawMode(shape3D.getDrawMode() == DrawMode.LINE ? DrawMode.FILL : DrawMode.LINE);
        }
    }

}
