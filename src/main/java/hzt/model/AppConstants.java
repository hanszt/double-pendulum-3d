package hzt.model;

import javafx.geometry.Dimension2D;
import javafx.scene.paint.Color;
import javafx.scene.transform.Translate;
import javafx.util.Duration;
import lombok.Getter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

import static javafx.scene.paint.Color.*;

public abstract class AppConstants {

    private static final Logger LOGGER = LogManager.getLogger(AppConstants.class);
    private static final Properties PROPS = configProperties();

    public static final int INIT_FRAME_RATE = 30; // f/s
    public static final Duration INIT_FRAME_DURATION = Duration.seconds(1. / INIT_FRAME_RATE); // s/f

    public static final Color INIT_BG_COLOR = NAVY;
    public static final Color INIT_HINGE_COLOR = ORANGE;
    public static final Color INIT_ARM_COLOR = RED;

    public static final Translate INIT_CAMERA_PLATFORM_TRANSLATION = new Translate(0, 0, -500);
    public static final double INIT_CAMERA_FOV = 30;
    public static final double INIT_MAX_CAMERA_VELOCITY = 100;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String TITLE = "Double Pendulum 3D";
    public static final String DOTTED_LINE = "----------------------------------------------------------------------------------------\n";
    public static final String CLOSING_MESSAGE = ANSI_BLUE + "See you next Time! :)" + ANSI_RESET;

    public static final double G = 9.81;
    public static final Dimension2D INIT_SCENE_DIMENSION;
    public static final Dimension2D MIN_STAGE_DIMENSION;
    public static final Dimension2D INIT_ANIMATION_PANE_DIMENSION;

    public static final double INIT_WORLD_TO_CAMERA_ANGLE_X;
    public static final double INIT_WORLD_TO_CAMERA_ANGLE_Y;
    public static final double STAGE_OPACITY;

    static {
        int sceneWidthProp = parsedIntProp("init_scene_width", 1200);
        int sceneHeightProp = parsedIntProp("init_scene_height", 800);
        int animationPaneWidthProp = parsedIntProp("init_animation_pane_width", 640);
        int animationPaneHeightProp = parsedIntProp("init_animation_pane_height", 640);
        INIT_SCENE_DIMENSION = new Dimension2D(sceneWidthProp, sceneHeightProp);
        MIN_STAGE_DIMENSION = determineMinStageDimension();
        INIT_ANIMATION_PANE_DIMENSION = new Dimension2D(animationPaneWidthProp, animationPaneHeightProp);
        INIT_WORLD_TO_CAMERA_ANGLE_X = parsedDoubleProp("init_angle_x", 0);
        INIT_WORLD_TO_CAMERA_ANGLE_Y = parsedDoubleProp("init_angle_y", 0);
        STAGE_OPACITY = parsedDoubleProp("stage_opacity", .8D);
    }

    private static double parsedDoubleProp(String property, double defaultVal) {
        double value = defaultVal;
        String propertyVal = PROPS.getProperty(property);
        try {
            value = Double.parseDouble(propertyVal);
        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.warn(String.format("Property '%s' with value '%s' could not be parsed to a double... " +
                    "Falling back to default: %f...", property, propertyVal, defaultVal ));
        }
        return value;
    }

    private static int parsedIntProp(String property, int defaultVal) {
        int value = defaultVal;
        String propertyVal = PROPS.getProperty(property);
        try {
            value = Integer.parseInt(propertyVal);
        } catch (NumberFormatException e) {
            LOGGER.warn(String.format("Property '%s' with value '%s' could not be parsed to an int... " +
                    "Falling back to default: %d...", property, propertyVal, defaultVal ));
        }
        return value;
    }

    private static Dimension2D determineMinStageDimension() {
        int defaultMinStageWidth = 750;
        int defaultMinStageHeight = 500;
        double minStageWidth = INIT_SCENE_DIMENSION.getWidth() < defaultMinStageWidth ?
                INIT_SCENE_DIMENSION.getWidth() : defaultMinStageWidth;
        double minStageHeight = INIT_SCENE_DIMENSION.getHeight() < defaultMinStageHeight ?
                INIT_SCENE_DIMENSION.getHeight() : defaultMinStageHeight;
        return new Dimension2D(minStageWidth, minStageHeight);
    }

    private static Properties configProperties() {
        Properties properties = new Properties();
        String pathName = "src/main/resources/app.properties";
        File file = new File(pathName);
        try (InputStream stream = new BufferedInputStream(new FileInputStream(file))) {
            properties.load(stream);
        } catch (IOException e) {
            LOGGER.warn(pathName + " not found...");
        }
        return properties;
    }


    @Getter
    public enum Scene {

        MAIN_SCENE("mainScene.fxml"),
        ABOUT_SCENE("aboutScene.fxml");

        private final String fxmlFileName;

        Scene(String fxmlFileName) {
            this.fxmlFileName = fxmlFileName;
        }

        public String formattedName() {
            return this.name().charAt(0) + this.name().substring(1).toLowerCase().replace("_", " ");
        }

    }

}
