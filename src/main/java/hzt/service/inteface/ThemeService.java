package hzt.service.inteface;

import hzt.model.appearance.Resource;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.util.Set;

public interface ThemeService {

    Resource DEFAULT_THEME = new Resource("Light", new File(""));

    Set<Resource> getThemes();

    ObjectProperty<Resource> currentThemeProperty();

    StringProperty styleSheetProperty();

}
