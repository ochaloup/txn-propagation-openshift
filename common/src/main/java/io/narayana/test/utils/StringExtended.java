package io.narayana.test.utils;

public final class StringExtended {
    private final String content;

    public StringExtended(String str) {
        this.content = str;
    }
    public StringExtended(StringBuilder strBuilder) {
        if(strBuilder == null) this.content = null;
        else this.content = strBuilder.toString();
    }

    /**
     * Returns content of the loaded string.
     *
     * @return string
     */
    public String get() {
        return content;
    }

    /**
     * Returns content of the loaded string if not empty {@link StringUtils#isNonEmpty(String)}.
     * Otherwise {@link IllegalStateException} is thrown.
     *
     * @return string
     * @throws IllegalStateException
     */
    public String getNonEmpty() {
        if(StringUtils.isEmpty(get())) {
            throw new IllegalStateException("String can't be empty");
        }
        return content;
    }

    @Override
    public String toString() {
        return get();
    }
}
