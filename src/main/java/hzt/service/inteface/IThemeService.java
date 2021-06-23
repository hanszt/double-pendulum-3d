package hzt.service.inteface;

import hzt.model.appearance.Resource;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public interface IThemeService {

    Resource DEFAULT_THEME = new Resource("Light", "");
    Resource DARK_THEME = new Resource("Dark", "style-dark.css");

    Iterable<Resource> getThemes();

    ObjectProperty<Resource> currentThemeProperty();

    StringProperty styleSheetProperty();

}
