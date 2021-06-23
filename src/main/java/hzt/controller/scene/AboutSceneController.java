package hzt.controller.scene;

import hzt.controller.SceneManager;
import hzt.service.AboutService;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.List;

import static hzt.model.AppConstants.INIT_SCENE_DIMENSION;
import static hzt.model.AppConstants.Scene.ABOUT_SCENE;
import static hzt.model.AppConstants.Scene.MAIN_SCENE;
import static hzt.service.AboutService.AboutText;

public class AboutSceneController extends SceneController {

    @FXML
    private ComboBox<AboutText> textComboBox;
    @FXML
    private TextArea textArea;

    private final AboutService aboutService = new AboutService();

    public AboutSceneController(SceneManager sceneManager) throws IOException {
        super(ABOUT_SCENE.getFxmlFileName(), sceneManager);
    }

    @Override
    public void setup() {
        textArea.setPrefSize(INIT_SCENE_DIMENSION.getWidth(), INIT_SCENE_DIMENSION.getHeight());
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

    protected SceneController getController() {
        return this;
    }

}
