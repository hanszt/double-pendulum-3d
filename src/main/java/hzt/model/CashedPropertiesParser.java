package hzt.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class CashedPropertiesParser implements PropertiesParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(CashedPropertiesParser.class);
    private static final Map<Path, PropertiesParser> propertyParsers = new HashMap<>();
    private final Properties properties;

    private CashedPropertiesParser(Properties properties) {
        this.properties = properties;
    }

    public double parsedDoubleProp(String property, double defaultVal) {
        String propertyVal = properties.getProperty(property, "default");
        try {
            return Double.parseDouble(propertyVal);
        } catch (NumberFormatException e) {
            LOGGER.warn(String.format("Property '%s' with value '%s' could not be parsed to a double... " +
                    "Falling back to default: %f...", property, propertyVal, defaultVal), e);
            return defaultVal;
        }
    }

    public int parsedIntProp(String property, int defaultVal) {
        String propertyVal = properties.getProperty(property);
        try {
            return Integer.parseInt(propertyVal);
        } catch (NumberFormatException e) {
            LOGGER.warn(String.format("Property '%s' with value '%s' could not be parsed to an int... " +
                    "Falling back to default: %d...", property, propertyVal, defaultVal));
            return defaultVal;
        }
    }

    public String property(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static PropertiesParser withSource(Path path) {
        final var parser = propertyParsers.get(path);
        if (parser != null) {
            return parser;
        }
        final var properties = new Properties();
        try (final var stream = new BufferedInputStream(Files.newInputStream(path))) {
            properties.load(stream);
        } catch (IOException e) {
            LOGGER.warn("{} not found...", path, e);
        }
        final var newParser = new CashedPropertiesParser(properties);
        propertyParsers.put(path, newParser);
        return newParser;
    }
}
