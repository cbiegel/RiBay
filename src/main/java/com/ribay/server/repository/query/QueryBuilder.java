package com.ribay.server.repository.query;

/**
 * Created by CD on 01.05.2016.
 */
public interface QueryBuilder<T> {

    public String buildQuery(T query);

}
