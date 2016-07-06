package com.ribay.server.exception;

/**
 * Created by CD on 04.07.2016.
 */
public class MissingFieldException extends Exception {

    private final String field;

    public MissingFieldException(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
