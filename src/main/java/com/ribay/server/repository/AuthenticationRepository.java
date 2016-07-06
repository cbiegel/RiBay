package com.ribay.server.repository;

import com.basho.riak.client.api.commands.indexes.BinIndexQuery;
import com.basho.riak.client.api.commands.indexes.IntIndexQuery;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.operations.DeleteOperation;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.LongIntIndex;
import com.basho.riak.client.core.query.indexes.StringBinIndex;
import com.google.common.primitives.Longs;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.User;
import com.ribay.server.util.RequestScopeData;
import com.ribay.server.util.riak.RiakObjectBuilder;
import com.ribay.server.util.RibayProperties;
import com.ribay.server.util.clock.RibayClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthenticationRepository {

    private static final String INDEX_NAME_TIME = "time";

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @Autowired
    private RibayClock clock;

    /**
     * @param sessionId
     * @return
     * @throws Exception use {@link RequestScopeData#getUser()} for getting the logged in user
     * @deprecated use
     */
    public User getLoggedInUser(final String sessionId) throws Exception {
        String bucket = properties.getBucketSessionLogin();
        Location location = new Location(new Namespace(bucket), sessionId);
        FetchValue fetchOp = new FetchValue.Builder(location).build();
        return client.execute(fetchOp).getValue(User.class);
    }

    public User login(final String sessionId, final User user) throws Exception {
        String bucket = properties.getBucketSessionLogin();

        Location location = new Location(new Namespace(bucket), sessionId);
        StoreValue storeOp = new StoreValue.Builder(user).withLocation(location).build();
        client.execute(storeOp); // TODO execute async?
        return user;
    }

    public Future<?> logout(final String sessionId) throws Exception {
        String bucket = properties.getBucketSessionLogin();
        Location location = new Location(new Namespace(bucket), sessionId);
        DeleteValue storeOp = new DeleteValue.Builder(location).build(); // TODO options
        return client.executeAsync(storeOp);
    }

    public void register(final User user) throws Exception {
        String bucket = properties.getBucketUsers();
        String key = user.getUuid().toString();
        Location location = new Location(new Namespace(bucket), key);

        // Set the email index for the new object. This requires a RiakObject type
        RiakObject riakObj = new RiakObjectBuilder(user) //
                .withIndex(StringBinIndex.named("index_email"), user.getEmailAddress()) //
                .build(); //

        // Store the object
        StoreValue storeOp = new StoreValue.Builder(riakObj).withLocation(location).build();
        client.execute(storeOp);
    }

    /**
     * Looks up existing user in the database by E-mail address.
     *
     * @param emailAddress The e-mail of the user (Secondary Index of the "users" bucket)
     * @return The user object of the user with the given e-mail, if it exists. If it does not
     * exist, returns null.
     * @throws Exception
     */
    public User lookupExistingUser(final String emailAddress) throws Exception {
        String bucket = properties.getBucketUsers();
        Namespace namespace = new Namespace(bucket);
        BinIndexQuery indexQuery = new BinIndexQuery.Builder(namespace, "index_email",
                emailAddress).build();
        BinIndexQuery.Response resp = client.execute(indexQuery);
        if (resp.hasEntries()) {
            Location location = resp.getEntries().get(0).getRiakObjectLocation();
            FetchValue fetchOp = new FetchValue.Builder(location).build();
            return client.execute(fetchOp).getValue(User.class);
        } else {
            return null;
        }
    }

    public Future<?> saveLastAccess(String sessionId) throws Exception {
        String bucket = properties.getBucketSessionLastAccess();

        String key = sessionId;
        long value = clock.getTime();

        // now store time for sessionId

        RiakObject obj = new RiakObjectBuilder(Longs.toByteArray(value)) //
                .withIndex(LongIntIndex.named(INDEX_NAME_TIME), value) //
                .build(); //

        Location cartObjectLocation = new Location(new Namespace(bucket), key);
        StoreValue storeOp = new StoreValue.Builder(obj).withLocation(cartObjectLocation).build();
        return client.executeAsync(storeOp); // exceute async
    }

    public List<String> getSessionIdsOlderThan(long periodInMs) throws Exception {
        long now = clock.getTime();

        long from = 0; // get all sessions ...
        long to = Long.max(now - periodInMs, 0); // ... that have not made a request for the specified interval (at least 0)

        String bucket = properties.getBucketSessionLastAccess();
        IntIndexQuery command = new IntIndexQuery.Builder(new Namespace(bucket), INDEX_NAME_TIME, from, to).build(); // range query
        IntIndexQuery.Response response = client.execute(command);

        List<String> result = response.getEntries().stream() // all entries in specified range
                // can not sort by indexKey -> NullPointerException
                //.sorted(Comparator.comparing(IntIndexQuery.Response.Entry::getIndexKey)) // sort by index value (meaning timestamp here)
                .map(IntIndexQuery.Response.Entry::getRiakObjectLocation) // map to location
                .map(Location::getKeyAsString) // map to key
                .collect(Collectors.toList()); // collect all keys
        return result;
    }

    public Future<?> deleteSessionLastAccess(String sessionId) throws Exception {
        String bucket = properties.getBucketSessionLastAccess();
        String key = sessionId;

        DeleteOperation command = new DeleteOperation.Builder(new Location(new Namespace(bucket), key)).build();
        return client.execute(command);
    }

}
