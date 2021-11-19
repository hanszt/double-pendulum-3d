package hzt.service.inteface;

import javafx.beans.property.StringProperty;

import java.util.Collection;

public interface IAboutService {

    Collection<AboutText> loadContent();

    record AboutText(String title, StringProperty text) {

        public String getText() {
            return text.get();
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
