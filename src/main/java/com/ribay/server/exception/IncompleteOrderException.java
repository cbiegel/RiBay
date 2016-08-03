package com.ribay.server.exception;

/**
 * Created by CD on 04.07.2016.
 */
public class IncompleteOrderException extends Exception {

    public IncompleteOrderException() {
        super("Order is incomplete!");
    }

}
