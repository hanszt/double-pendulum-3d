package hzt.service.inteface;

import hzt.model.appearance.Resource;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.net.URL;
import java.util.Optional;

public interface IThemeService {

    Resource DEFAULT_THEME = new Resource("Light", new File(""));
    Resource DARK_THEME = new Resource("Dark",
            Optional.ofNullable(IThemeService.class.getResource("../../../css/style-dark.css"))
                    .map(URL::getFile)
                    .map(File::new)
                    .orElse(new File("")));

    Iterable<Resource> getThemes();

    ObjectProperty<Resource> currentThemeProperty();

    StringProperty styleSheetProperty();

}
