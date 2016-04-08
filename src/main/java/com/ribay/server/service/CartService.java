package com.ribay.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ribay.server.material.Cart;
import com.ribay.server.repository.CartRepository;
import com.ribay.server.util.RequestScopeData;

@RestController
public class CartService
{

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private RequestScopeData requestData;

    @RequestMapping(path = "/cart", method = RequestMethod.GET)
    public Cart getCart() throws Exception
    {
        String sessionId = requestData.getSessionId();

        // TODO comment the next line to prevent getting only the same demo data
        sessionId = DemoService.DEMO_SESSION_ID;

        return cartRepository.getCart(sessionId);
    }
}
