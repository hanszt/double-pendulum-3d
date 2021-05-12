package hzt.service;

import javafx.beans.property.SimpleStringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AboutService implements IAboutService {

    private static final Logger LOGGER = LogManager.getLogger(AboutService.class);
    private static final String RELATIVE_TEXT_RESOURCE_DIR = "../../about";

    public List<AboutText> loadContent() {
        var url = getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR);
        if (url != null) {
            var fileDir = new File(url.getFile());
            if (fileDir.isDirectory()) {
                return Arrays.stream(fileDir.list())
                        .map(this::toAboutText)
                        .collect(Collectors.toList());
            } else LOGGER.error("about folder not found at " + RELATIVE_TEXT_RESOURCE_DIR + "...");
        }
        return List.of(new AboutText("No content", new SimpleStringProperty()));
    }

    private AboutText toAboutText(String fileName) {
        var name = fileName.replace(".txt", "").replace("_", " ");
        return new AboutText(name, new SimpleStringProperty(loadTextContent(fileName)));
    }

    private String loadTextContent(String fileName) {
        try {
            var url = getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR + "/" + fileName);
            return String.join(String.format("%n"), Files.readAllLines(new File(url.getFile()).toPath()));
        } catch (NullPointerException | IOException e) {
            LOGGER.error(() -> "Invalid resource url...");
            return "";
        }
    }
}
