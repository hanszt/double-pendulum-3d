package hzt.view;

import hzt.controller.AppManager;
import hzt.model.CachedPropertiesParser;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.time.Clock;

public final class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        new AppManager(
                stage,
                CachedPropertiesParser.withSource(Path.of("src/main/resources/app.properties")),
                Clock.systemUTC()
        ).start();
    }

}
