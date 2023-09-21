package hzt.service.inteface;

import javafx.geometry.Point3D;
import javafx.scene.Node;

public interface MouseControlService {

    void initMouseControl(Node node);

    void setOrientation(double angleX, double angleY);

    void setTargetTranslation(Point3D point3D);

    double getTargetTranslateX();

    double getTargetTranslateY();

    double getTargetTranslateZ();

    double getAngleX();

    double getAngleY();
}
