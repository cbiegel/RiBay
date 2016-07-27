package com.ribay.server.material;

import java.util.List;

/**
 * Created by CD on 08.07.2016.
 */
public class OrderSearchResult {

    private List<OrderFinished> orders;
    private String continuation;

    public OrderSearchResult() {
    }

    public List<OrderFinished> getOrders() {
        return orders;
    }

    public String getContinuation() {
        return continuation;
    }

    public void setOrders(List<OrderFinished> orders) {
        this.orders = orders;
    }

    public void setContinuation(String continuation) {
        this.continuation = continuation;
    }
}
