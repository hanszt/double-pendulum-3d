package hzt.view;

import hzt.controller.AppManager;
import hzt.model.CashedPropertiesParser;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.Clock;

public final class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        new AppManager(
                stage,
                CashedPropertiesParser.withSource(Path.of("src/main/resources/app.properties")),
                Clock.systemUTC()
        ).start();
    }

}
