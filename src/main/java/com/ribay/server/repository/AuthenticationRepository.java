package com.ribay.server.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.User;
import com.ribay.server.util.RibayProperties;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthenticationRepository
{
    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    public User getLoggedInUser(final String sessionId) throws Exception
    {
        String bucket = properties.getBucketSessionLogin();
        Location location = new Location(new Namespace(bucket), sessionId);
        FetchValue fetchOp = new FetchValue.Builder(location).build();
        return client.execute(fetchOp).getValue(User.class);
    }

    public User login(final String sessionId, final User user) throws Exception
    {
        String bucket = properties.getBucketSessionLogin();

        Location location = new Location(new Namespace(bucket), sessionId);
        StoreValue storeOp = new StoreValue.Builder(user).withLocation(location).build();
        client.execute(storeOp); // TODO execute async?
        return user;
    }

    public void logout(final String sessionId) throws Exception
    {
        String bucket = properties.getBucketSessionLogin();
        Location location = new Location(new Namespace(bucket), sessionId);
        DeleteValue storeOp = new DeleteValue.Builder(location).build(); // TODO options
        client.execute(storeOp); // TODO execute async?
    }
}
