package com.ribay.server.repository;

import com.ribay.server.material.OrderFinished;
import com.ribay.server.material.OrderQuery;
import com.ribay.server.material.PageInfo;
import com.ribay.server.repository.query.QueryBuilderOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CD on 06.07.2016.
 */
public class OrderRepository {

    public void storeFinishedOrder(OrderFinished order) throws Exception {
        // TODO: store order
        // use a fact for each order because number of orders can be really high
        // this also prevents duplicate orders when using the order id as key
        // add search index so trader can query all orders and users can query their orders using filters
    }

    public List<OrderFinished> getAllOrders(PageInfo pageInfo) throws Exception {
        OrderQuery allOrderQuery = new OrderQuery();
        allOrderQuery.setUserId(null); // show orders of all users
        allOrderQuery.setDateFrom(null); // do not filter by date
        allOrderQuery.setDateTo(null); // do not filter by date
        allOrderQuery.setPageInfo(pageInfo);
        return queryOrders(allOrderQuery);
    }

    public List<OrderFinished> getUserOrders(String userId, PageInfo pageInfo) throws Exception {
        OrderQuery allOrderQuery = new OrderQuery();
        allOrderQuery.setUserId(userId); // show orders of specified user
        allOrderQuery.setDateFrom(null); // do not filter by date
        allOrderQuery.setDateTo(null); // do not filter by date
        allOrderQuery.setPageInfo(pageInfo);
        return queryOrders(allOrderQuery);
    }

    private List<OrderFinished> queryOrders(OrderQuery orderQuery) throws Exception {
        String query = new QueryBuilderOrder().buildQuery(orderQuery);
        PageInfo pageInfo = orderQuery.getPageInfo();
        // TODO implement
        return new ArrayList<>();
    }

}
