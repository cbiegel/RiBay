package com.ribay.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.Cart;
import com.ribay.server.util.AuthInterceptor;
import com.ribay.server.util.RequestScopeData;
import com.ribay.server.util.RibayProperties;

@RestController
public class CartService {

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @Autowired
    private RequestScopeData requestData;

    @RequestMapping(path = "/cart", method = RequestMethod.GET)
    public Cart getCart() throws Exception {
        String sessionId = requestData.getSessionId();

        // TODO comment the next line to prevent getting only the same demo data
        sessionId = DemoService.DEMO_SESSION_ID;

        Namespace quotesBucket = new Namespace(properties.getBucketCart());
        Location quoteObjectLocation = new Location(quotesBucket, sessionId);

        FetchValue fetchOp = new FetchValue.Builder(quoteObjectLocation).build();
        return client.execute(fetchOp).getValue(Cart.class);
    }

}
