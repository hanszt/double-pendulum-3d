package hzt.utils;

import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.function.Consumer;
import java.util.stream.Stream;

public final class FxUtils {

    private FxUtils() {
    }

    public static Stream<Node> allDescendents(Parent root) {
        return allDescendents(root, Node.class);
    }

    public static <N extends Node> Stream<N> allDescendents(Parent root, Class<N> type) {
        final var builder = Stream.<N>builder();
        addAllDescendents(root, type, builder);
        return builder.build();
    }

    private static <N extends Node> void addAllDescendents(Parent parent, Class<N> type, Consumer<N> nodes) {
        for (var node : parent.getChildrenUnmodifiable()) {
            if (type.isInstance(node)) {
                nodes.accept(type.cast(node));
            }
            if (node instanceof Parent subParent) {
                addAllDescendents(subParent, type, nodes);
            }
        }
    }
}
