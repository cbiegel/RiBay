package com.ribay.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by CD on 04.07.2016.
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User is not correct")
public class IncorrectUserException extends Exception {
}
