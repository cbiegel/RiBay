package com.ribay.server.exception;

import com.ribay.server.material.Cart;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by CD on 04.07.2016.
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Cart has changed.")
public class CartChangedException extends Exception {

    private final Cart expected;
    private final Cart given;

    public CartChangedException(Cart expected, Cart given) {
        this.expected = expected;
        this.given = given;
    }

    public Cart getExpected() {
        return expected;
    }

    public Cart getGiven() {
        return given;
    }
}
