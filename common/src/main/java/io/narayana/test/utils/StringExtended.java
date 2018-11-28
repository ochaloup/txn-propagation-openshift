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

    public String get() {
        return content;
    }

    public String nonEmpty() {
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
