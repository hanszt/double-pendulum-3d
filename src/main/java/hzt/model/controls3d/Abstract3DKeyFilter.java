package hzt.model.controls3d;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public abstract class Abstract3DKeyFilter {

    private static final double INIT_USER_INPUT_VECTOR_SIZE = 100.0;

    final KeyCode negXKeyCode;
    final KeyCode posXKeycode;
    final KeyCode negYKeyCode;
    final KeyCode posYKeyCode;
    final KeyCode negZKeyCode;
    final KeyCode posZKeyCode;

    private boolean xNegPressed;
    private boolean xPosPressed;
    private boolean yNegPressed;
    private boolean yPosPressed;
    private boolean zNegPressed;
    private boolean zPosPressed;

    private final DoubleProperty userInputVectorSize = new SimpleDoubleProperty(INIT_USER_INPUT_VECTOR_SIZE);

    private final EventHandler<KeyEvent> keyPressed;
    private final EventHandler<KeyEvent> keyReleased;

    Abstract3DKeyFilter(
            KeyCode negXKeyCode, KeyCode posXKeycode,
            KeyCode negYKeyCode, KeyCode posYKeyCode,
            KeyCode negZKeyCode, KeyCode posZKeyCode) {
        keyPressed = this::keyPressed;
        keyReleased = this::keyReleased;
        this.negXKeyCode = negXKeyCode;
        this.posXKeycode = posXKeycode;
        this.negYKeyCode = negYKeyCode;
        this.posYKeyCode = posYKeyCode;
        this.negZKeyCode = negZKeyCode;
        this.posZKeyCode = posZKeyCode;
    }

    private static final Point3D X_POS_DIR = new Point3D(1, 0, 0);
    private static final Point3D Y_POS_DIR = new Point3D(0, 1, 0);
    private static final Point3D Z_POS_DIR = new Point3D(0, 0, 1);

    private void keyPressed(KeyEvent e) {
            if (pressedKey(e, posXKeycode, xPosPressed)) xPosPressed = pressedAction(X_POS_DIR.multiply(userInputVectorSize.get()));
            if (pressedKey(e, negXKeyCode, xNegPressed)) xNegPressed = pressedAction(X_POS_DIR.multiply(-userInputVectorSize.get()));
            if (pressedKey(e, posYKeyCode, yPosPressed)) yPosPressed = pressedAction(Y_POS_DIR.multiply(userInputVectorSize.get()));
            if (pressedKey(e, negYKeyCode, yNegPressed)) yNegPressed = pressedAction(Y_POS_DIR.multiply(-userInputVectorSize.get()));
            if (pressedKey(e, posZKeyCode, zPosPressed)) zPosPressed = pressedAction(Z_POS_DIR.multiply(userInputVectorSize.get()));
            if (pressedKey(e, negZKeyCode, zNegPressed)) zNegPressed = pressedAction(Z_POS_DIR.multiply(-userInputVectorSize.get()));
    }

    private static boolean pressedKey(KeyEvent enteredKey, KeyCode comparedKey, boolean isPressed) {
        return enteredKey.getCode() == comparedKey && !isPressed;
    }

    private void keyReleased(KeyEvent e) {
        if (e.getCode() == posXKeycode) xPosPressed = releasedAction(X_POS_DIR.multiply(userInputVectorSize.get()));
        if (e.getCode() == negXKeyCode) xNegPressed = releasedAction(X_POS_DIR.multiply(-userInputVectorSize.get()));
        if (e.getCode() == posYKeyCode) yPosPressed = releasedAction(Y_POS_DIR.multiply(userInputVectorSize.get()));
        if (e.getCode() == negYKeyCode) yNegPressed = releasedAction(Y_POS_DIR.multiply(-userInputVectorSize.get()));
        if (e.getCode() == posZKeyCode) zPosPressed = releasedAction(Z_POS_DIR.multiply(userInputVectorSize.get()));
        if (e.getCode() == negZKeyCode) zNegPressed = releasedAction(Z_POS_DIR.multiply(-userInputVectorSize.get()));
        var allReleased = !(xNegPressed || xPosPressed || yNegPressed || yPosPressed || zNegPressed || zPosPressed);
        allReleasedAction(allReleased);
    }

    abstract boolean pressedAction(Point3D point3D);

    abstract boolean releasedAction(Point3D point3D);

    abstract void allReleasedAction(boolean allReleased);

    public DoubleProperty userInputVectorSizeProperty() {
        return userInputVectorSize;
    }

    public EventHandler<KeyEvent> getKeyPressed() {
        return keyPressed;
    }

    public EventHandler<KeyEvent> getKeyReleased() {
        return keyReleased;
    }
}
