package com.ribay.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by CD on 04.07.2016.
 */
@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED, reason = "Cart is empty")
public class EmptyCartException extends Exception {

    public EmptyCartException() {
        super("Cart is empty");
    }

}
