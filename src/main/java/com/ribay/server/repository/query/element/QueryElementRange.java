package com.ribay.server.repository.query.element;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryElementRange implements QueryElement {

    private final String field;
    private final Number from;
    private final Number to;
    private final String extra;

    public QueryElementRange(String field, Number from, Number to) {
        this(field, from, to, null);
    }

    public QueryElementRange(String field, Number from, Number to, String extra) {
        this.field = field;
        this.from = from;
        this.to = to;
        this.extra = extra;
    }

    @Override
    public String toQuery() {
        String fromString = (from == null) ? "*" : from.toString();
        String toString = (to == null) ? "*" : to.toString();
        if ((from == null) && (to == null)) {
            return "";
        } else if (extra == null) {
            return String.format("(%s:[%s TO %s])", field, fromString, toString);
        } else {
            return String.format("(%s:[%s TO %s] %s)", field, fromString, toString, extra);
        }
    }

}
