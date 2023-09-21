package hzt.service;

import hzt.model.appearance.Resource;
import hzt.service.inteface.ThemeService;
import hzt.utils.StringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class StandardThemeService implements ThemeService {

    private static final String RELATIVE_STYLE_SHEET_RESOURCE_DIR = "/css";

    private static final Logger LOGGER = LoggerFactory.getLogger(StandardThemeService.class);

    private final StringProperty styleSheet = new SimpleStringProperty();
    private final ObjectProperty<Resource> currentTheme = new SimpleObjectProperty<>();

    private final Set<Resource> themes;

    public StandardThemeService() {
        themes = scanForThemeStyleSheets();
        currentTheme.addListener((observableValue, theme, newTheme) -> loadThemeStyleSheet(newTheme));
    }

    private Set<Resource> scanForThemeStyleSheets() {
        Set<Resource> themeSet = new HashSet<>(Set.of(DEFAULT_THEME));
        var url = getClass().getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR);
        if (url != null) {
            var styleDirectory = new File(url.getFile());
            if (styleDirectory.isDirectory()) {
                var fileNames = styleDirectory.listFiles();
                for (var file : Objects.requireNonNull(fileNames)) {
                    var themeName = extractThemeName(file.getName());
                    themeSet.add(new Resource(themeName, file));
                }
            }
        } else {
            LOGGER.error("Stylesheet resource folder not found...");
        }
        return Set.copyOf(themeSet);
    }

    private static String extractThemeName(String fileName) {
        return StringUtils.convertToCapitalizedSentence(fileName, "style", ".css");
    }

    private void loadThemeStyleSheet(Resource theme) {
        try {
            final var styleSheetUrl = theme.getUrl();
            LOGGER.debug("{}", styleSheetUrl);
            styleSheet.set(styleSheetUrl.toExternalForm());
            LOGGER.atDebug().setMessage(styleSheetUrl::toExternalForm).log();
        } catch (MalformedURLException e) {
            LOGGER.error("stylesheet of {} could not be loaded...", theme.name());
        }
    }

    public StringProperty styleSheetProperty() {
        return styleSheet;
    }

    public ObjectProperty<Resource> currentThemeProperty() {
        return currentTheme;
    }

    public Set<Resource> getThemes() {
        return Set.copyOf(themes);
    }

}
