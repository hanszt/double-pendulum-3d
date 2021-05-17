package hzt.service;

import hzt.model.appearance.Resource;
import hzt.utils.StringUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ThemeService implements IThemeService {

    private static final String RELATIVE_STYLE_SHEET_RESOURCE_DIR = "../../css";

    private static final Logger LOGGER = LogManager.getLogger(ThemeService.class);

    private final StringProperty styleSheet = new SimpleStringProperty();
    private final ObjectProperty<Resource> currentTheme = new SimpleObjectProperty<>();

    private final Set<Resource> themes;

    public ThemeService() {
        this.themes = scanForThemeStyleSheets();
        currentTheme.addListener((observableValue, theme, newTheme) -> loadThemeStyleSheet(newTheme));
    }

    private Set<Resource> scanForThemeStyleSheets() {
        Set<Resource> themeSet = new HashSet<>(Set.of(DEFAULT_THEME, DARK_THEME));
        var url = getClass().getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR);
        if (url != null) {
            var styleDirectory = new File(url.getFile());
            if (styleDirectory.isDirectory()) {
                var fileNames = styleDirectory.list();
                for (var fileName : fileNames) {
                    var themeName = extractThemeName(fileName);
                    themeSet.add(new Resource(themeName, fileName));
                }
            }
        } else LOGGER.error("Stylesheet resource folder not found...");
        return themeSet;
    }

    private String extractThemeName(String fileName) {
        var themeName = fileName
                .replace("style-", "")
                .replace('-', ' ')
                .replace(".css", "");
        themeName = StringUtils.toOnlyFirstLetterUpperCase(themeName);
        return themeName;
    }

    private void loadThemeStyleSheet(Resource theme) {
        var styleSheetUrl = getClass()
                .getResource(RELATIVE_STYLE_SHEET_RESOURCE_DIR + "/" + theme.getFileName());
        if (styleSheetUrl != null) {
            LOGGER.debug(styleSheetUrl);
            this.styleSheet.set(styleSheetUrl.toExternalForm());
            LOGGER.debug(styleSheetUrl::toExternalForm);
        } else LOGGER.error(() -> "stylesheet of " + theme.getName() + " could not be loaded...");
    }

    public StringProperty styleSheetProperty() {
        return styleSheet;
    }

    public ObjectProperty<Resource> currentThemeProperty() {
        return currentTheme;
    }

    public Set<Resource> getThemes() {
        return themes;
    }

}
