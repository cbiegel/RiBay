package com.ribay.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by CD on 30.08.2016.
 */
@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "Invalid register")
public class InvalidRegisterException extends Exception {

    public InvalidRegisterException() {
        super("Invalid register");
    }

}
