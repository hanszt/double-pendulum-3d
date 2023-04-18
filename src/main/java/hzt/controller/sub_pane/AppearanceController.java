package hzt.controller.sub_pane;

import hzt.controller.FXMLController;
import hzt.controller.scene.MainSceneController;
import hzt.controller.scene.SceneController;
import hzt.model.DoublePendulum;
import hzt.model.appearance.Resource;
import hzt.model.trace.TraceXY;
import hzt.service.BackgroundService;
import hzt.service.ThemeService;
import hzt.service.inteface.IBackgroundService;
import hzt.service.inteface.IThemeService;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static hzt.model.AppConstants.*;
import static java.lang.Boolean.TRUE;
import static javafx.scene.layout.BackgroundPosition.*;
import static javafx.scene.layout.BackgroundRepeat.*;

public class AppearanceController extends FXMLController {


    private static final double STAGE_OPACITY = parsedDoubleProp("stage_opacity", 0.8D);

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

    private final IThemeService themeService = new ThemeService();
    private final IBackgroundService backgroundService = new BackgroundService();
    private final MainSceneController mainSceneController;
    private final Collection<DoublePendulum> doublePendulums;


    public AppearanceController(MainSceneController mainSceneController,
                                Collection<DoublePendulum> doublePendulums) throws IOException {
        super("appearancePane.fxml");
        this.mainSceneController = mainSceneController;
        this.doublePendulums = Collections.unmodifiableCollection(doublePendulums);
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
        themeService.getThemes().forEach(theme -> themeCombobox.getItems().add(theme));
        themeService.currentThemeProperty().bind(themeCombobox.valueProperty());
        themeService.styleSheetProperty().addListener(this::changeStyle);
        themeCombobox.setValue(IThemeService.DEFAULT_THEME);

        backgroundService.getResources().forEach(r -> backgroundCombobox.getItems().add(r));
        backgroundCombobox.setValue(BackgroundService.NO_PICTURE);
    }

    public void changeStyle(ObservableValue<? extends String> o, String c, String newVal) {
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
        opacityStageButton.selectedProperty().addListener((o, c, n) -> stage.setOpacity(TRUE.equals(n) ? STAGE_OPACITY : 1));
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
        backgroundCombobox.setValue(BackgroundService.NO_PICTURE);
    }

    @FXML
    private void pendulumMassColorPickerAction() {
        doublePendulums.forEach(DoublePendulum::update);
    }

    @FXML
    private void heightLightColorPickerAction() {
        doublePendulums.forEach(DoublePendulum::update);
    }

    @Override
    protected FXMLController getController() {
        return this;
    }
}
