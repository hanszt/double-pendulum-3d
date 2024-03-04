package hzt.controller.sub_pane;

import hzt.controller.FXMLController;
import hzt.controller.scene.MainSceneController;
import hzt.controller.scene.SceneController;
import hzt.model.DoublePendulum;
import hzt.model.PropertiesParser;
import hzt.model.appearance.Resource;
import hzt.model.trace.TraceXY;
import hzt.service.StandardBackgroundService;
import hzt.service.StandardThemeService;
import hzt.service.inteface.BackgroundService;
import hzt.service.inteface.ThemeService;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.Boolean.TRUE;
import static java.util.Comparator.comparing;
import static javafx.scene.layout.BackgroundPosition.CENTER;
import static javafx.scene.layout.BackgroundRepeat.NO_REPEAT;
import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.ORANGE;
import static javafx.scene.paint.Color.RED;
import static javafx.scene.paint.Color.WHEAT;

public final class AppearanceController extends FXMLController {


    public static final Color INIT_BG_COLOR = WHEAT;
    private static final Color INIT_HINGE_COLOR = ORANGE;
    private static final Color INIT_ARM_COLOR = RED;
    private static final Color INIT_TRACE_COLOR = GREEN;

    @FXML
    private ColorPicker traceColorPicker;
    @FXML
    private ToggleButton opacityStageButton;
    @FXML
    private ComboBox<Resource> backgroundCombobox;
    @FXML
    private ComboBox<Resource> themeCombobox;
    @FXML
    private ToggleButton fullScreenButton;
    @FXML
    private ColorPicker backgroundColorPicker;
    @FXML
    private ColorPicker pendulumColorPicker;
    @FXML
    private ColorPicker pendulumMassColorPicker;

    private final ThemeService themeService = new StandardThemeService();
    private final BackgroundService backgroundService = new StandardBackgroundService();
    private final MainSceneController mainSceneController;
    private final Collection<DoublePendulum> doublePendulums;
    private final double stageOpacity;


    public AppearanceController(MainSceneController mainSceneController,
                                Collection<DoublePendulum> doublePendulums,
                                PropertiesParser propertiesParser) throws IOException {
        super("appearancePane.fxml");
        this.mainSceneController = mainSceneController;
        this.doublePendulums = Collections.unmodifiableCollection(doublePendulums);
        stageOpacity = propertiesParser.parsedDoubleProp("stage_opacity", 0.8D);
        configureComboBoxes();
    }

    public void configureColorPickers() {
        backgroundColorPicker.setValue(INIT_BG_COLOR);
        pendulumMassColorPicker.setValue(INIT_HINGE_COLOR);
        pendulumColorPicker.setValue(INIT_ARM_COLOR);
        traceColorPicker.setValue(INIT_TRACE_COLOR);
        doublePendulums.forEach(this::bindColorProperties);
    }

    private void bindColorProperties(DoublePendulum doublePendulum) {
        doublePendulum.massColorProperty().bind(pendulumMassColorPicker.valueProperty());
        doublePendulum.armMaterial.diffuseColorProperty().bind(pendulumColorPicker.valueProperty());
        doublePendulum.armMaterial.specularColorProperty().bind(pendulumColorPicker.valueProperty());
    }

    public void bindTraceColorToTraceColorPicker(TraceXY traceXY) {
        traceXY.colorProperty().bind(traceColorPicker.valueProperty());
    }

    private void configureComboBoxes() {
        final List<Resource> themes = themeService.getThemes().stream()
                .sorted(comparing(Resource::name))
                .toList();

        themeCombobox.getItems().addAll(themes);
        themeService.currentThemeProperty().bind(themeCombobox.valueProperty());
        themeService.styleSheetProperty().addListener((o, c, newVal) -> changeStyle(newVal));
        themeCombobox.setValue(ThemeService.DEFAULT_THEME);

        final List<Resource> backgrounds = backgroundService.getResources().stream()
                .sorted(comparing(Resource::name))
                .toList();

        backgroundCombobox.getItems().addAll(backgrounds);
        backgroundCombobox.setValue(StandardBackgroundService.NO_PICTURE);
    }

    public void changeStyle(String newVal) {
        var sceneManager = mainSceneController.getSceneManager();
        var sceneControllers = sceneManager.getSceneControllerMap().values();
        for (SceneController sceneController : sceneControllers) {
            var styleSheets = sceneController.getScene().getStylesheets();
            styleSheets.removeIf(filter -> !styleSheets.isEmpty());
            if (newVal != null) {
                styleSheets.add(newVal);
            }
        }
    }

    public void configureStageControlButtons(Stage stage) {
        fullScreenButton.setOnAction(e -> stage.setFullScreen(!stage.isFullScreen()));
        stage.fullScreenProperty().addListener((o, c, n) -> fullScreenButton.setSelected(n));
        stage.addEventFilter(KeyEvent.KEY_PRESSED, key -> setFullScreenWhenF11Pressed(stage, key));
        bindStageOpacityToOpacityButton(stage);
    }

    private static void setFullScreenWhenF11Pressed(Stage stage, KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.F11) {
            stage.setFullScreen(!stage.isFullScreen());
        }
    }

    private void bindStageOpacityToOpacityButton(Stage stage) {
        opacityStageButton.selectedProperty().addListener((o, c, n) -> stage.setOpacity(TRUE.equals(n) ? stageOpacity : 1));
    }

    @FXML
    private void backgroundComboBoxAction() {
        Optional.of(backgroundCombobox.getValue().file())
                .filter(file -> !file.getPath().isBlank())
                .map(AppearanceController::toInputStream)
                .ifPresentOrElse(this::changeBackgroundImage, this::backgroundColorPickerAction);
    }

    private static InputStream toInputStream(File file) {
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void changeBackgroundImage(InputStream inputStream) {
        var animationPane = mainSceneController.getAnimationPane();
        var background = new Background(new BackgroundImage(new Image(inputStream),
                NO_REPEAT, NO_REPEAT, CENTER, new BackgroundSize(animationPane.getWidth(), animationPane.getHeight(),
                false, false, false, true)));
        animationPane.setBackground(background);
    }

    @FXML
    private void backgroundColorPickerAction() {
        mainSceneController.getAnimationPane().setBackground(new Background(
                new BackgroundFill(backgroundColorPicker.getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
        backgroundCombobox.setValue(StandardBackgroundService.NO_PICTURE);
    }

    public void highlightSelectedPendulum(DoublePendulum pendulum) {
        playColorTransition(pendulum.massColorProperty(), pendulumMassColorPicker.valueProperty());
        playColorTransition(pendulum.armMaterial.diffuseColorProperty(), pendulumColorPicker.valueProperty());
    }

    private void playColorTransition(ObjectProperty<Color> colorObjectProperty, ObjectProperty<Color> bound) {
        boolean noOtherColorTransitionPlaying = colorObjectProperty.isBound();
        if (noOtherColorTransitionPlaying) {
            Color current = colorObjectProperty.get();
            ColorTransition colorTransition = new ColorTransition(Duration.seconds(1), current.invert(), current, colorObjectProperty::set);
            colorObjectProperty.unbind();
            colorTransition.setOnFinished(e -> colorObjectProperty.bind(bound));
            colorTransition.play();
        }
    }

    private static final class ColorTransition extends Transition {
        private final Color fromColor;
        private final Color toColor;
        private final Consumer<Color> colorObjectProperty;

        public ColorTransition(Duration duration, Color fromColor, Color toColor, Consumer<Color> colorConsumer) {
            setCycleDuration(duration);
            this.fromColor = fromColor;
            this.toColor = toColor;
            this.colorObjectProperty = colorConsumer;
        }

        @Override
        protected void interpolate(double frac) {
            colorObjectProperty.accept(fromColor.interpolate(toColor, frac));
        }
    }
}
