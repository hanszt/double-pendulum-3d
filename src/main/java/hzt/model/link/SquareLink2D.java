package hzt.model.link;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Box;

public class SquareLink2D extends Link2D<Box> {

    public SquareLink2D() {
        super(new Box());
    }

    @Override
    public DoubleProperty heightProperty() {
        return getShape3D().heightProperty();
    }

}
