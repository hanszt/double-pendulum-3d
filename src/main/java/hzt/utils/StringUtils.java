package hzt.utils;

public final class StringUtils {

    private StringUtils() {
    }

    public static String toOnlyFirstLetterUpperCase(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static String convertToCapitalizedSentence(String string, String... stringsToRemove) {
        String result = string
                .replace('-', ' ')
                .replace('_', ' ');
        for (String stringToRemove : stringsToRemove) {
            result = result.replace(stringToRemove, "");
        }
        return StringUtils.toOnlyFirstLetterUpperCase(result.strip());
    }

}
