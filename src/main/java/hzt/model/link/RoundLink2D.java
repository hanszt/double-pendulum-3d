package hzt.model.link;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Cylinder;

public class RoundLink2D extends Link2D {

    public RoundLink2D() {
        super(new Cylinder());
    }

    @Override
    public DoubleProperty heightProperty() {
        return ((Cylinder) shape3D).heightProperty();
    }
}
