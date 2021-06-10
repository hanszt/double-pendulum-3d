package hzt.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public abstract class FXMLController {

    private static final String FXML_FILE_LOCATION = "../../../fxml/";

    private final Parent root;

    protected FXMLController(String fxmlFileName) throws IOException {
        var loader = new FXMLLoader();

        //to be able to pass arguments to the constructor, it's necessary to specify the controller in the controller factory of the loader
        loader.setControllerFactory(c -> getController());
        loader.setLocation(getClass().getResource(FXML_FILE_LOCATION + fxmlFileName));
        this.root = loader.load();
    }

    protected abstract FXMLController getController();

    public Parent getRoot() {
        return root;
    }
}
