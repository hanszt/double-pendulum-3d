package hzt.service;

import hzt.service.inteface.IAboutService;
import hzt.utils.StringUtils;
import javafx.beans.property.SimpleStringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AboutService implements IAboutService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutService.class);
    private static final String RELATIVE_TEXT_RESOURCE_DIR = "../../about";

    public final Set<AboutText> loadContent() {
        LOGGER.info("Loading content...");
        return Optional.ofNullable(getClass().getResource(RELATIVE_TEXT_RESOURCE_DIR))
                .map(AboutService::toAboutTexts)
                .orElseGet(AboutService::logErrorAndGetDefault);
    }

    private static Set<AboutText> logErrorAndGetDefault() {
        LOGGER.error("about folder not found at {}...", RELATIVE_TEXT_RESOURCE_DIR);
        return Set.of(new AboutText("No content", new SimpleStringProperty()));
    }

    private static Set<AboutText> toAboutTexts(URL url) {
        try (var paths = Files.walk(new File(url.getFile()).toPath())) {
            return paths.map(Path::toFile)
                    .filter(File::isFile)
                    .map(AboutService::toAboutText)
                    .collect(Collectors.toUnmodifiableSet());
        } catch (IOException e) {
            LOGGER.error("Something went wrong", e);
            return Collections.emptySet();
        }
    }

    private static AboutText toAboutText(File file) {
        var name = StringUtils.convertToCapitalizedSentence(file.getName(), ".txt");
        return new AboutText(name, new SimpleStringProperty(loadTextContent(file)));
    }

    private static String loadTextContent(File file) {
        try {
            return String.join(String.format("%n"), Files.readAllLines(file.toPath()));
        } catch (IOException e) {
            LOGGER.error("Invalid resource url...", e);
            return "";
        }
    }
}
