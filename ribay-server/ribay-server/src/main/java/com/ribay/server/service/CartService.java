package com.ribay.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.MyRiakClient;
import com.ribay.server.RibayProperties;
import com.ribay.server.material.Cart;

@RestController
public class CartService
{

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @CrossOrigin(origins = "*")
    @RequestMapping("/cart/get/")
    public Cart getCart() throws Exception
    {
        // TODO get session id from cookie
        String sessionId = DemoService.DEMO_SESSION_ID;

        Namespace quotesBucket = new Namespace(properties.getBucketCart());
        Location quoteObjectLocation = new Location(quotesBucket, sessionId);

        FetchValue fetchOp = new FetchValue.Builder(quoteObjectLocation).build();
        return client.execute(fetchOp).getValue(Cart.class);
    }

}
