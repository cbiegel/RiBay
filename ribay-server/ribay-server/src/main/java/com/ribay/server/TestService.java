package com.ribay.server;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;

@RestController
public class TestService
{

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private MyRiakClient client;

    @CrossOrigin(origins = "*")
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name)
    {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/greeting-riak")
    public String greetingRiak(@RequestParam(value = "name", defaultValue = "german") String name)
            throws Exception
    {
        Namespace quotesBucket = new Namespace("welcome");
        Location quoteObjectLocation = new Location(quotesBucket, name);

        FetchValue fetchOp = new FetchValue.Builder(quoteObjectLocation).build();
        RiakObject fetchedObject = client.execute(fetchOp).getValue(RiakObject.class);
        return fetchedObject.getValue().toStringUtf8();
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/get-welcome")
    public Welcome getWelcome(@RequestParam(value = "name", defaultValue = "german") String name)
            throws Exception
    {
        Namespace quotesBucket = new Namespace("welcome2");
        Location quoteObjectLocation = new Location(quotesBucket, name);

        FetchValue fetchOp = new FetchValue.Builder(quoteObjectLocation).build();
        return client.execute(fetchOp).getValue(Welcome.class);
    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/put-welcome")
    public Welcome putWelcome(@RequestParam(value = "key") String key,
            @RequestParam(value = "value") String value) throws Exception
    {
        Welcome welcome = new Welcome(key, value);

        Namespace quotesBucket = new Namespace("welcome2");
        Location quoteObjectLocation = new Location(quotesBucket, welcome.getLanguage());
        StoreValue storeOp = new StoreValue.Builder(welcome).withLocation(quoteObjectLocation)
                .build();

        StoreValue.Response response = client.execute(storeOp);
        return response.getValue(Welcome.class);
    }

}
