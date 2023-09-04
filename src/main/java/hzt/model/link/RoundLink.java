package hzt.model.link;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Cylinder;

public class RoundLink extends Link<Cylinder> {

    public RoundLink() {
        super(new Cylinder());
    }

    @Override
    public DoubleProperty lengthProperty() {
        return getShape3D().heightProperty();
    }
}
