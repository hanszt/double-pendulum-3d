package hzt.view;

import hzt.controller.AppManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        new AppManager(stage).start();
    }

}
