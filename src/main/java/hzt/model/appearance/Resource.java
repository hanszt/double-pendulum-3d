package hzt.model.appearance;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public record Resource(String name, File file) {

    public URL getUrl() throws MalformedURLException {
        return file.toURI().toURL();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Resource resource = (Resource) o;
        return Objects.equals(name, resource.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
