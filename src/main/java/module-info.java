module doublependulum3d {

    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires org.jetbrains.annotations;
    requires kotlin.stdlib;

    opens hzt.controller.scene to javafx.fxml;
    opens hzt.controller.sub_pane to javafx.fxml;
    opens hzt.utils;

    exports hzt.view to javafx.graphics;
}
