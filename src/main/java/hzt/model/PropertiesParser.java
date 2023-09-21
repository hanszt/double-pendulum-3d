package hzt.model;

public interface PropertiesParser {
    double parsedDoubleProp(String property, double defaultVal);

    int parsedIntProp(String property, int defaultVal);

    String property(String key, String defaultValue);
}
