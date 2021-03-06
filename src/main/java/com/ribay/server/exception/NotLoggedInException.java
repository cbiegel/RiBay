package com.ribay.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by CD on 04.07.2016.
 */
@ResponseStatus(code = HttpStatus.PRECONDITION_FAILED, reason = "Must be logged in")
public class NotLoggedInException extends Exception {

    public NotLoggedInException() {
        super("Must be logged in!");
    }

}
