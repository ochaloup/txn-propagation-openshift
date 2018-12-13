package io.narayana.test.properties;

import io.narayana.test.utils.StringExtended;

public final class EnvVariables {
    private EnvVariables() {}

    // -- environment variables
    // temporary directory
    public static final String TMP_DIR_PARAM = "java.io.tmpdir";
    // location of home directory
    public static final String HOME_DIR_PARAM = "user.home";
    // basedir is used when run with maven as directory where mvn base directory is located
    public static final String BASE_DIR_PARAM = "basedir";

    /**
     * On provided parameter returns value which is known based of the provided configuration.
     */
    public static String get(final String paramName) {
        return System.getProperty(paramName);
    }

    /**
     * On provided parameter returns value which is known based of the provided configuration
     * while returning {@link StringExtended}.
     */
    public static StringExtended getE(final String paramName) {
        return new StringExtended(get(paramName));
    }
}
