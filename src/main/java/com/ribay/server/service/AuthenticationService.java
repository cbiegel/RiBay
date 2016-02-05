package com.ribay.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.User;
import com.ribay.server.util.AuthInterceptor;
import com.ribay.server.util.RequestScopeData;
import com.ribay.server.util.RibayProperties;

@RestController
public class AuthenticationService
{

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @Autowired
    private RequestScopeData requestData;

    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = AuthInterceptor.HEADER_NAME)
    @RequestMapping(path = "/auth/loggedin", method = RequestMethod.GET)
    public User getLoggedInUser() throws Exception
    {
        String bucket = properties.getBucketSessionLogin();
        String key = requestData.getSessionId();
        Location location = new Location(new Namespace(bucket), key);

        FetchValue fetchOp = new FetchValue.Builder(location).build();
        User value = client.execute(fetchOp).getValue(User.class);
        return value;
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = AuthInterceptor.HEADER_NAME)
    @RequestMapping(path = "/auth/login", method = RequestMethod.POST)
    public User login(@RequestParam(value = "username") String userName,
            @RequestParam(value = "password") String password) throws Exception
    {
        // TODO encrypt password and send as header
        // TODO do login against DB
        if (userName.equals("test") && password.equals("test"))
        {
            String bucket = properties.getBucketSessionLogin();
            String key = requestData.getSessionId();
            User value = new User("test");

            Location location = new Location(new Namespace(bucket), key);
            StoreValue storeOp = new StoreValue.Builder(value).withLocation(location).build();
            client.execute(storeOp); // TODO execute async?

            return value;
        }
        else
        {
            return null;
        }
    }

    @CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = AuthInterceptor.HEADER_NAME)
    @RequestMapping(path = "/auth/logout", method = RequestMethod.POST)
    public void logout() throws Exception
    {
        String bucket = properties.getBucketSessionLogin();
        String key = requestData.getSessionId();

        Location location = new Location(new Namespace(bucket), key);
        DeleteValue storeOp = new DeleteValue.Builder(location).build(); // TODO options
        client.execute(storeOp); // TODO execute async?
    }

}
