package hzt.service.inteface;

import javafx.geometry.Point3D;
import javafx.scene.Node;

public interface IMouseControlService {

    void initMouseControl(Node node);

    void setOrientation(double initAngleX, double initAngleY);

    void setTargetTranslation(Point3D point);

    double getTargetTranslateX();

    double getTargetTranslateY();

    double getTargetTranslateZ();

    double getAngleX();

    double getAngleY();
}
