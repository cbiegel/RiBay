package com.ribay.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by CD on 02.05.2016.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such element")
public class NotFoundException extends Exception {

}
