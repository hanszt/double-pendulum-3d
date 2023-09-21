package hzt.controller.scene;

import hzt.controller.SceneManager;
import hzt.model.PropertiesParser;
import hzt.service.inteface.AboutService;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.time.Clock;
import java.util.List;

import static hzt.controller.SceneManager.Scene.ABOUT_SCENE;
import static hzt.controller.SceneManager.Scene.MAIN_SCENE;
import static hzt.service.StandardAboutService.AboutText;

public class AboutSceneController extends SceneController {

    @FXML
    private ComboBox<AboutText> textComboBox;
    @FXML
    private TextArea textArea;

    private final AboutService aboutService;

    public AboutSceneController(SceneManager sceneManager,
                                AboutService aboutService,
                                PropertiesParser propertiesParser,
                                Clock clock) throws IOException {
        super(ABOUT_SCENE.getFxmlFileName(), propertiesParser, sceneManager, clock);
        this.aboutService = aboutService;
    }

    @Override
    protected void setup() {
        Dimension2D sceneDimension = initSceneDimension;
        textArea.setPrefSize(sceneDimension.getWidth(), sceneDimension.getHeight());
        textArea.setEditable(false);
        List<AboutText> aboutTextList = textComboBox.getItems();
        aboutTextList.addAll(aboutService.loadContent());
        textComboBox.setValue(aboutTextList.get(0));
    }

    @FXML
    public void goBack() {
        sceneManager.setupScene(MAIN_SCENE);
    }

    @FXML
    private void textComboboxAction() {
        textArea.setText(textComboBox.getValue().getText());
    }

}
