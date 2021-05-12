package hzt.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static hzt.model.AppConstants.G;
import static java.lang.Math.*;

public class DoublePendulum extends Group {

    public static final String BALL_TRACE = "Ball trace";
    public static final String LINK_TRACE = "Linked trace";
    public static final String NO_TRACE = "No trace";

    private final Cylinder anchor;
    private final PendulumCylinder upperPendulum;
    private final PendulumCylinder lowerPendulum;
    private final PhongMaterial armMaterial = new PhongMaterial();
    private final Link upperArm;
    private final Link lowerArm;

    private final Map<String, TraceXY> traceTypeMap = new HashMap<>();
    private final ObjectProperty<String> traceType = new SimpleObjectProperty<>();

    private final ObjectProperty<Color> massColor = new SimpleObjectProperty<>();

    private final DoubleProperty angle1 = new SimpleDoubleProperty();
    private final DoubleProperty angle2 = new SimpleDoubleProperty();
    private final DoubleProperty vAngular1 = new SimpleDoubleProperty();
    private final DoubleProperty vAngular2 = new SimpleDoubleProperty(Math.toRadians(10));
    private final DoubleProperty aAngular1 = new SimpleDoubleProperty();
    private final DoubleProperty aAngular2 = new SimpleDoubleProperty();

    private final DoubleProperty friction = new SimpleDoubleProperty();

    public DoublePendulum() {
        this.anchor = new Cylinder();
        traceTypeMap.put(BALL_TRACE, new BallTrace());
        traceTypeMap.put(LINK_TRACE, new LinkTrace());
        traceTypeMap.put(NO_TRACE, noTrace());
        this.upperPendulum = new PendulumCylinder();
        this.lowerPendulum = new PendulumCylinder();
        this.upperArm = new Link();
        this.lowerArm = new Link();
        this.getChildren().addAll(anchor, upperArm, lowerArm);
        upperPendulum.addWeightToFxGroup(this);
        lowerPendulum.addWeightToFxGroup(this);
        configureComponents();
        traceType.addListener(this::traceTypeChanged);
    }

    private TraceXY noTrace() {
        return new TraceXY() {
            @Override
            void updateTrace(Point2D pointXY) {
                //nothing to update...
            }

            @Override
            public void clear() {
                // nothing to clear...
            }
        };
    }

    private void traceTypeChanged(ObservableValue<? extends String> o, String c, String n) {
        TraceXY curTrace = traceTypeMap.get(c);
        if (curTrace != null) {
            curTrace.clear();
            getChildren().remove(curTrace);
        }
        TraceXY newTrace = traceTypeMap.get(n);
        if (newTrace != null) {
            newTrace.setTranslateZ(anchor.getHeight());
            getChildren().add(newTrace);
        }
    }

    private void configureComponents() {
        configureArms();
        configureAnchor();
        bindColorProperties();
        updatePendulumPosition();
    }

    private void configureArms() {
        upperArm.defineStartAndEndpoint(anchor, upperPendulum.getCylinder());
        lowerArm.defineStartAndEndpoint(upperPendulum.getCylinder(), lowerPendulum.getCylinder());
        upperArm.setMaterial(armMaterial);
        lowerArm.setMaterial(armMaterial);
    }

    private static final int DEFAULT_ANCHOR_WIDTH = 10;

    private void configureAnchor() {
        anchor.setRotationAxis(Rotate.X_AXIS);
        anchor.setRotate(90);
        anchor.setRadius(2);
        anchor.setHeight(DEFAULT_ANCHOR_WIDTH);
        var material = new PhongMaterial();
        material.diffuseColorProperty().bind(massColor);
        anchor.setMaterial(material);
    }

    private void bindColorProperties() {
        upperPendulum.diffuseColorProperty().bind(massColor);
        lowerPendulum.diffuseColorProperty().bind(massColor);
    }

    public void update() {
        calculateAngularAcc1();
        calculateAngularAcc2();
        calculateAngleVars();
        setBoundToAngles();
        updatePendulumPosition();
        traceTypeMap.get(traceType.get()).update(lowerPendulum.getPositionXY());
        simulateFriction();
    }

    private void updatePendulumPosition() {
        upperPendulum.setPosition(sin(angle1.get()) * upperArm.getHeight(), cos(angle1.get()) * upperArm.getHeight());
        var posLowerMassWithRespectToUpperMass =
                new Point2D(sin(angle2.get()) * lowerArm.getHeight(), cos(angle2.get()) * lowerArm.getHeight());
        var positionUpperMass = upperPendulum.getPositionXY();
        lowerPendulum.setPosition(positionUpperMass.getX() + posLowerMassWithRespectToUpperMass.getX(),
                positionUpperMass.getY() + posLowerMassWithRespectToUpperMass.getY());
    }

    private void calculateAngularAcc1() {
        var num1 = -G * (2 * upperPendulum.getMass() + lowerPendulum.getMass()) * sin(angle1.get());
        var num2 = -lowerPendulum.getMass() * G * sin(angle1.get() - 2 * angle2.get());
        var num3 = -2 * sin(angle1.get() - angle2.get()) * lowerPendulum.getMass();
        var num4 = vAngular2.get() * vAngular2.get() * lowerArm.getHeight() + vAngular1.get() * vAngular1.get() *
                upperArm.getHeight() * cos(angle1.get() - angle2.get());
        var den = upperArm.getHeight() * (2 * upperPendulum.getMass() * lowerPendulum.getMass() - lowerPendulum.getMass() *
                cos(2 * angle1.get() - 2 * angle2.get()));
        aAngular1.set((num1 + num2 + num3 * num4) / den);
    }

    private void calculateAngularAcc2() {
        var num1 = 2 * sin(angle1.get() - angle2.get());
        var num2 = vAngular1.get() * vAngular1.get() * upperArm.getHeight() * (upperPendulum.getMass() + lowerPendulum.getMass());
        var num3 = G * (upperPendulum.getMass() + lowerPendulum.getMass()) * cos(angle1.get());
        var num4 = vAngular2.get() * vAngular2.get() * lowerArm.getHeight() * lowerPendulum.getMass() * cos(angle1.get() - angle2.get());
        var den = lowerArm.getHeight() * (2 * upperPendulum.getMass() * lowerPendulum.getMass() - lowerPendulum.getMass() *
                cos(2 * angle1.get() - 2 * angle2.get()));
        aAngular2.set((num1 * (num2 + num3 + num4)) / den);
    }

    private void calculateAngleVars() {
        vAngular1.set(vAngular1.get() + aAngular1.get());
        angle1.set(angle1.get() + vAngular1.get());
        vAngular2.set(vAngular2.get() + aAngular2.get());
        angle2.set(angle2.get() + vAngular2.get());
    }

    private void setBoundToAngles() {
        if (angle1.get() > PI) angle1.set(angle1.get() - 2 * PI);
        if (angle1.get() < -PI) angle1.set(angle1.get() + 2 * PI);
        if (angle2.get() > PI) angle2.set(angle2.get() - 2 * PI);
        if (angle2.get() < -PI) angle2.set(angle2.get() + 2 * PI);
    }

    private void simulateFriction() {
        vAngular1.set(vAngular1.get() * (1 - friction.get()));
        vAngular2.set(vAngular2.get() * (1 - friction.get()));
    }

    public void reset() {
        vAngular1.set(0);
        vAngular2.set(0);
        aAngular1.set(0);
        aAngular2.set(0);
    }

    public void clearTrace() {
        traceTypeMap.values().forEach(TraceXY::clear);
    }

    public int traceSize() {
        return traceTypeMap.get(traceType.get()).size();
    }

    public DoubleProperty upperArmLengthProperty() {
        return upperArm.heightProperty();
    }

    public double getUpperArmLength() {
        return upperArm.getHeight();
    }

    public DoubleProperty lowerArmLengthProperty() {
        return lowerArm.heightProperty();
    }

    public double getLowerArmLength() {
        return lowerArm.getHeight();
    }

    public ObjectProperty<Color> massColorProperty() {
        return massColor;
    }

    public ObjectProperty<Color> armColorProperty() {
        return armMaterial.diffuseColorProperty();
    }

    public PendulumCylinder getUpperPendulum() {
        return upperPendulum;
    }

    public PendulumCylinder getLowerPendulum() {
        return lowerPendulum;
    }

    public double getAngle1Deg() {
        return Math.toDegrees(angle1.get());
    }

    public DoubleProperty angle1Property() {
        return angle1;
    }

    public double getAngle2Deg() {
        return Math.toDegrees(angle2.get());
    }

    public DoubleProperty angle2Property() {
        return angle2;
    }

    public double getVAngular1deg() {
        return Math.toDegrees(vAngular1.get());
    }

    public DoubleProperty vAngular1Property() {
        return vAngular1;
    }

    public double getVAngular2Deg() {
        return Math.toDegrees(vAngular2.get());
    }

    public DoubleProperty vAngular2Property() {
        return vAngular2;
    }

    public double getAAngular1Deg() {
        return Math.toDegrees(aAngular1.get());
    }

    public double getAAngular2Deg() {
        return Math.toDegrees(aAngular2.get());
    }

    public double getFriction() {
        return friction.get();
    }

    public DoubleProperty frictionProperty() {
        return friction;
    }

    public ObjectProperty<String> traceTypeProperty() {
        return traceType;
    }

    public Collection<TraceXY> traceTypes() {
        return traceTypeMap.values();
    }

}
