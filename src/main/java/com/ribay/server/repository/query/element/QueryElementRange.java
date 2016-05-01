package com.ribay.server.repository.query.element;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryElementRange implements QueryElement {

    private final String field;
    private final Number from;
    private final Number to;

    public QueryElementRange(String field, Number from, Number to) {
        this.field = field;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toQuery() {
        String fromString = (from == null) ? "*" : from.toString();
        String toString = (to == null) ? "*" : to.toString();
        return (from == null) && (to == null) ? "" : String.format("%s:[%s TO %s]", field, fromString, toString);
    }

}
