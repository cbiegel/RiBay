package com.ribay.server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.Cart;
import com.ribay.server.util.RibayProperties;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CartRepository
{

    @Autowired
    private RibayProperties properties;

    @Autowired
    private MyRiakClient client;

    public Cart getCart(final String sessionId) throws Exception
    {
        Namespace quotesBucket = new Namespace(properties.getBucketCart());
        Location quoteObjectLocation = new Location(quotesBucket, sessionId);

        FetchValue fetchOp = new FetchValue.Builder(quoteObjectLocation).build();
        return client.execute(fetchOp).getValue(Cart.class);
    }
}