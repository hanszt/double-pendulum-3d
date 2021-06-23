package hzt.service;

import hzt.service.inteface.IAboutService;
import hzt.utils.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AboutService implements IAboutService {

    private static final Logger LOGGER = LogManager.getLogger(AboutService.class);
    private static final String RELATIVE_TEXT_RESOURCE_DIR = "../../about";

    public List<AboutText> loadContent() {
        List<AboutText> aboutTextList = new ArrayList<>();
        var optUrl = Optional.ofNullable(getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR));
        optUrl.ifPresentOrElse(url -> fillAboutTextList(url, aboutTextList), () -> logErrorAndAddDefault(aboutTextList));
        return aboutTextList;
    }

    private void logErrorAndAddDefault(List<AboutText> aboutTextList) {
        LOGGER.error("about folder not found at " + RELATIVE_TEXT_RESOURCE_DIR + "...");
        aboutTextList.add(new AboutText("No content", new SimpleStringProperty()));
    }

    private void fillAboutTextList(URL url, List<AboutText> aboutTextList) {
        try (var paths = Files.walk(new File(url.getFile()).toPath())) {
            paths.map(Path::toFile)
                    .filter(File::isFile)
                    .map(this::toAboutText)
                    .forEach(aboutTextList::add);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private AboutText toAboutText(File file) {
        var name = file.getName().replace(".txt", "").replace("_", " ");
        name = StringUtils.toOnlyFirstLetterUpperCase(name);
        return new AboutText(name, new SimpleStringProperty(loadTextContent(file)));
    }

    private String loadTextContent(File file) {
        try {
            return String.join(String.format("%n"), Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            LOGGER.error(() -> "Invalid resource url...");
            return "";
        }
    }
}
