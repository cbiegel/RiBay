package com.ribay.server.exception;

import com.ribay.server.material.Cart;
import com.ribay.server.material.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by CD on 04.07.2016.
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Cart has changed.")
public class CartChangedException extends Exception {

    private final Order newOrder;

    public CartChangedException(Order newOrder) {
        this.newOrder = newOrder;
    }

    public Order getNewOrder() {
        return newOrder;
    }

}
