package com.ribay.server.exception;

/**
 * Created by CD on 29.06.2016.
 */
public class IncorrectHashException extends Exception {

    private final String expected;
    private final String found;

    public IncorrectHashException(String expected, String found) {
        super(String.format("Incorrect hash. expected: %s found: %s", expected, found));
        this.expected = expected;
        this.found = found;
    }

    public String getExpected() {
        return expected;
    }

    public String getFound() {
        return found;
    }
}
