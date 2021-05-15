package hzt.utils;

public class StringUtils {

    private StringUtils() {
    }

    public static String toOnlyFirstLetterUpperCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

}
