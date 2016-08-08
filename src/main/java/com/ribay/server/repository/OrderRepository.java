package com.ribay.server.repository;

import com.basho.riak.client.api.commands.indexes.IntIndexQuery;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.operations.StoreOperation;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.LongIntIndex;
import com.basho.riak.client.core.util.BinaryValue;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.OrderFinished;
import com.ribay.server.material.OrderSearchResult;
import com.ribay.server.util.RibayProperties;
import com.ribay.server.util.clock.RibayClock;
import com.ribay.server.util.riak.RiakObjectBuilder;
import com.ribay.server.util.riak.SecondaryIndexHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by CD on 06.07.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class OrderRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRepository.class);

    private static final String USER_ID_FOR_STORING_ALL_ORDERS = "0";
    private static final String INDEX_NAME_ORDER_TIMESTAMP = "index_timestamp";

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @Autowired
    private RibayClock clock;

    @Autowired
    private SecondaryIndexHelper secondaryIndexHelper;


    public void storeFinishedOrder(OrderFinished order) throws Exception {
        RiakFuture<?, ?> f1 = storeFinishedOrderForUser(order, USER_ID_FOR_STORING_ALL_ORDERS); // store order in bucket for all users
        RiakFuture<?, ?> f2 = storeFinishedOrderForUser(order, order.getUserId()); // store order in bucket for user that ordered
        f1.get();
        f2.get();
    }

    private RiakFuture<?, ?> storeFinishedOrderForUser(OrderFinished order, String userId) throws Exception {
        // use a fact for each order because number of orders can be really high
        // this also prevents duplicate orders when using the order id as key
        // add search index so trader can query all orders and users can query their orders using filters

        Namespace bucket = properties.getBucketOrders(userId);
        String key = order.getId();
        Location location = new Location(bucket, key);

        RiakObject value = new RiakObjectBuilder(order) //
                .withIndex(LongIntIndex.named(INDEX_NAME_ORDER_TIMESTAMP), toIndexValue(order.getTimestamp())) //
                .build(); //

        // TODO more options for query? (sloppy quorum, timeout, nval, etc.)
        StoreOperation command = new StoreOperation.Builder(location).withContent(value).build();
        RiakFuture<StoreOperation.Response, Location> response = client.execute(command);
        return response;
    }

    public OrderSearchResult getAllOrders(String continuation) throws Exception {
        return getUserOrders(USER_ID_FOR_STORING_ALL_ORDERS, continuation);
    }

    public OrderSearchResult getUserOrders(String userId, String continuation) throws Exception {
        Namespace bucket = properties.getBucketOrders(userId);

        long now = clock.getTime();

        long start = toIndexValue(now - (365 * 24 * 60 * 60 * 1000)); // one year ago
        long end = toIndexValue(now); // until now

        IntIndexQuery biq = new IntIndexQuery.Builder(bucket, INDEX_NAME_ORDER_TIMESTAMP, Math.min(start, end), Math.max(start, end))
                .withMaxResults(5)
                .withPaginationSort(true)
                .withContinuation((continuation == null) ? null : BinaryValue.create(continuation))
                .build();

        IntIndexQuery.Response response = client.execute(biq);

        List<OrderFinished> orders = secondaryIndexHelper.fetchValues(response, OrderFinished.class);
        String newContinuation = response.hasContinuation() ? response.getContinuation().toStringUtf8() : null;

        OrderSearchResult result = new OrderSearchResult();
        result.setOrders(orders);
        result.setContinuation(newContinuation);
        return result;
    }

    private long toIndexValue(long timestamp) {
        // reverse so orders with higher timestamp will have lower index value -> will be found earlier
        return Long.MAX_VALUE - timestamp;
    }

}
