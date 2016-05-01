package com.ribay.server.repository.query.element;

/**
 * Created by CD on 01.05.2016.
 */
@FunctionalInterface
public interface QueryElement {

    /**
     * Returns the query as String or an empty String is the query is a tautology
     * @return
     */
    public String toQuery();

}
