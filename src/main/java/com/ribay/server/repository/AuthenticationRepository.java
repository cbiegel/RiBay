package com.ribay.server.repository;

import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.StringBinIndex;
import com.basho.riak.client.core.util.BinaryValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.basho.riak.client.api.commands.indexes.BinIndexQuery;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.User;
import com.ribay.server.util.RibayProperties;

import javax.websocket.Decoder;

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

    public void register(final User user) throws Exception
    {
        String bucket = properties.getBucketUsers();
        Location location = new Location(new Namespace(bucket), user.getUuid().toString());
        // Set the email index for the new object. This requires a RiakObject type
        String jsonString = new ObjectMapper().writeValueAsString(user);
        RiakObject riakObj = new RiakObject();
        riakObj.setContentType("application/json");
        riakObj.setValue(BinaryValue.create(jsonString));
        riakObj.getIndexes().getIndex(StringBinIndex.named("index_email")).add(user.getEmailAddress());
        // Store the object
        StoreValue storeOp = new StoreValue.Builder(riakObj).withLocation(location).build();
        client.execute(storeOp);
    }

    /**
     * Looks up existing user in the database by E-mail address.
     * 
     * @param emailAddress
     *            The e-mail of the user (Secondary Index of the "users" bucket)
     * @return The user object of the user with the given e-mail, if it exists. If it does not
     *         exist, returns null.
     * @throws Exception
     */
    public User lookupExistingUser(final String emailAddress) throws Exception
    {
        String bucket = properties.getBucketUsers();
        Namespace namespace = new Namespace(bucket);
        BinIndexQuery indexQuery = new BinIndexQuery.Builder(namespace, "index_email",
                emailAddress).build();
        BinIndexQuery.Response resp = client.execute(indexQuery);
        if (resp.hasEntries())
        {
            Location location = resp.getEntries().get(0).getRiakObjectLocation();
            FetchValue fetchOp = new FetchValue.Builder(location).build();
            return client.execute(fetchOp).getValue(User.class);
        }
        else
        {
            return null;
        }
    }
}
