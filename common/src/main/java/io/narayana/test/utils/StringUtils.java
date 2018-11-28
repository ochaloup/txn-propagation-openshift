package io.narayana.test.utils;

public final class StringUtils {
    private StringUtils() {}

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNonEmpty(String str) {
        return !isEmpty(str);
    }
}
