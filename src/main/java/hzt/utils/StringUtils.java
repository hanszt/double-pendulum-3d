package hzt.utils;

import java.util.Arrays;
import java.util.Locale;

public final class StringUtils {

    private StringUtils() {
    }

    public static String toOnlyFirstLetterUpperCase(String string) {
        return string.substring(0, 1).toUpperCase(Locale.ROOT) + string.substring(1).toLowerCase(Locale.ROOT);
    }

    public static String convertToCapitalizedSentence(String string, String... stringsToRemove) {
        String initial = string
                .replace('-', ' ')
                .replace('_', ' ');
        String wantedRemoved = Arrays.stream(stringsToRemove).reduce(initial, (acc, s) -> acc.replace(s, ""));
        return toOnlyFirstLetterUpperCase(wantedRemoved.strip());
    }

}
