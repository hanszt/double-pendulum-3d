package hzt.model.appearance;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public record Resource(String name, File file) {

    public URL getUrl() throws MalformedURLException {
        return file.toURI().toURL();
    }

    @Override
    public String toString() {
        return name;
    }
}
