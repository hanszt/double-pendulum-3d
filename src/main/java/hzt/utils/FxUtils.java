package hzt.utils;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape3D;

import java.util.ArrayList;
import java.util.List;

public final class FxUtils {

    private FxUtils() {
    }

    public static List<Node> allDescendents(Parent root) {
        List<Node> nodes = new ArrayList<>();
        addAllDescendents(root, nodes, Node.class);
        return nodes;
    }

    public static <N extends Node> List<N> allDescendents(Parent root, Class<N> type) {
        List<N> nodes = new ArrayList<>();
        addAllDescendents(root, nodes, type);
        return nodes;
    }

    private static <N extends Node> void addAllDescendents(Parent parent, List<N> nodes, Class<N> type) {
        for (var node : parent.getChildrenUnmodifiable()) {
            if (type.isInstance(node)) {
                nodes.add(type.cast(node));
            }
            if (node instanceof Parent subParent) {
                addAllDescendents(subParent, nodes, type);
            }
        }
    }

    public static void setDrawMode(Shape3D shape3D) {
        if (!shape3D.drawModeProperty().isBound()) {
            shape3D.setDrawMode(DrawMode.LINE == shape3D.getDrawMode() ? DrawMode.FILL : DrawMode.LINE);
        }
    }

}
