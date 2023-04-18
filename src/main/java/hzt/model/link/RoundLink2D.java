package hzt.model.link;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Cylinder;

public class RoundLink2D extends Link2D<Cylinder> {

    public RoundLink2D() {
        super(new Cylinder());
    }

    @Override
    public DoubleProperty heightProperty() {
        return getShape3D().heightProperty();
    }
}
