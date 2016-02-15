package com.ribay.server.service;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.ArticleShort;
import com.ribay.server.material.Cart;
import com.ribay.server.util.RibayProperties;

@RestController
public class DemoService {

    private final Logger logger = LoggerFactory.getLogger(DemoService.class);

    public static final String DEMO_SESSION_ID = "demo_key";

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    /**
     * Creates demo objects for demo UI. This will override the existing data in the demo buckets
     *
     * @throws Exception
     */
    @RequestMapping(path = "/demo/create", method = RequestMethod.POST)
    public void createDemo() throws Exception {
        logger.info("create demo");

        Cart demoCart = new Cart(Arrays.asList(
                new ArticleShort("abc", "My first article in shopping cart",
                        "http://placehold.it/200x400", 12.34, 2),
                new ArticleShort("def", "2nd article", "http://placehold.it/400x200", 5.99, 1),
                new ArticleShort("ghi", "last article in this cart", "http://placehold.it/50x50",
                        1.99, 10)));

        Namespace cartBucket = new Namespace(properties.getBucketCart());
        Location cartObjectLocation = new Location(cartBucket, DEMO_SESSION_ID);

        StoreValue storeOp = new StoreValue.Builder(demoCart).withLocation(cartObjectLocation)
                .build();

        client.execute(storeOp);

        // TODO create more demo data
    }

}
