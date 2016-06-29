package com.ribay.server.repository.query;

import com.ribay.server.material.OrderQuery;
import com.ribay.server.repository.query.element.QueryElementAnd;
import com.ribay.server.repository.query.element.QueryElementEqual;
import com.ribay.server.repository.query.element.QueryElementRange;

import java.util.Date;

/**
 * Created by CD on 25.06.2016.
 */
public class QueryBuilderOrder implements QueryBuilder<OrderQuery> {

    @Override
    public String buildQuery(OrderQuery query) {

        QueryElementAnd and = new QueryElementAnd();

        if (query.getUserId() != null) {
            and.addClause(new QueryElementEqual("userId", query.getUserId()));
        }
        Date dateFrom = query.getDateFrom();
        Date dateTo = query.getDateTo();
        and.addClause(new QueryElementRange("timestamp", ((dateFrom == null) ? null : dateFrom.getTime()), ((dateTo == null) ? null : dateTo.getTime())));

        return and.toQuery();
    }

}
