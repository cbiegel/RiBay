package com.ribay.server.service;

import com.ribay.server.exception.*;
import com.ribay.server.material.Cart;
import com.ribay.server.material.Order;
import com.ribay.server.material.OrderFinished;
import com.ribay.server.material.converter.Converter;
import com.ribay.server.repository.CartRepository;
import com.ribay.server.util.RequestScopeData;
import com.ribay.server.util.RibayConstants;
import com.ribay.server.util.clock.RibayClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by CD on 04.07.2016.
 */
@RestController
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RequestScopeData requestData;

    @Autowired
    private RibayClock clock;

    @Autowired
    private Converter<Order, OrderFinished> orderConverter;

    @RequestMapping(path = "/checkout/start", method = RequestMethod.POST)
    public Order startCheckout() throws Exception {
        if (requestData.getUser() == null) {
            // must be logged in
            throw new NotLoggedInException();
        }

        String sessionId = requestData.getSessionId();
        String userId = requestData.getUser().getUuid().toString();

        Cart cart = cartRepository.getCart(sessionId);
        if (cart.getArticles().isEmpty()) {
            // cart must not be empty
            throw new EmptyCartException();
        }

        // refresh data that might have changed after articles were added to cart (especially prices)
        cart = getRefreshedCart(cart);

        // generate id
        String id = UUID.randomUUID().toString();
        long time = clock.getTime();

        Order order = new Order();
        order.setId(id);
        order.setSessionId(sessionId);
        order.setUserId(userId);
        order.setCart(cart);
        order.setAddress(null); // no address yet
        order.setDateStarted(time);
        order.updateHash();
        return order;
    }

    @RequestMapping(path = "/checkout/finish", method = RequestMethod.POST)
    public OrderFinished finishCheckout(@RequestBody Order order) throws Exception {

        // throws exception when order was manipulated by client
        order.checkHash();

        long now = clock.getTime();
        long diffTime = now - order.getDateStarted();

        if (diffTime > RibayConstants.MAX_AGE_OF_UNFINISHED_ORDER_IN_MS) {
            // if order started too long ago
            throw new OrderTooOldException();
        }

        if (requestData.getUser() == null) {
            // must be logged in
            throw new NotLoggedInException();
        }

        String sessionId = requestData.getSessionId();
        String userId = requestData.getUser().getUuid().toString();

        if (!sessionId.equals(order.getSessionId())) {
            // client that started the order must be the same as the client that finishes the order
            throw new IncorrectClientException();
        }
        if (!userId.equals(order.getUserId())) {
            // user that started the order must be the same as the user that finishes the order
            throw new IncorrectUserException();
        }

        Cart cartFromDB = cartRepository.getCart(sessionId);
        Cart cartFromDBRefreshed = getRefreshedCart(cartFromDB);

        Cart cartFromClient = order.getCart();

        if (hasCartChanged(cartFromDBRefreshed, cartFromClient)) {
            // if cart has changed -> client has to confirm new cart explicitly
            throw new CartChangedException(cartFromDBRefreshed, cartFromClient);
        }

        if (order.getAddress() == null) {
            // address is missing
            throw new MissingFieldException("address");
        }

        // TODO more preconditions?
        // TODO can client handle exceptions? f.e. can client use info about outdated cart to confirm new cart?

        // ### now everything is alright - finish order ### //

        OrderFinished orderFinished = orderConverter.convert(order);

        // TODO delete cart
        // TODO store order in db

        return orderFinished;
    }

    private boolean hasCartChanged(Cart c1, Cart c2) {
        // TODO implement equals method or provide other check. otherwise this will always state that the carts are different when they are not the same instance
        return !c1.equals(c2);
    }

    private Cart getRefreshedCart(Cart cart) {
        // TODO check price and update cart (maybe update db?)
        return cart;
    }

}
