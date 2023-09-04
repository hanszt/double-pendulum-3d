package hzt.model.link;

import javafx.beans.property.DoubleProperty;
import javafx.scene.shape.Box;

public class SquareLink extends Link<Box> {

    public SquareLink() {
        super(new Box());
    }

    @Override
    public DoubleProperty lengthProperty() {
        return getShape3D().heightProperty();
    }

}
