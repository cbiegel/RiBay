package com.ribay.server.repository.query.element;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryElementTextRange implements QueryElement {

    private final String field;
    private final String from;
    private final String to;
    private final boolean prefix;

    public QueryElementTextRange(String field, String from, String to, boolean prefix) {
        this.field = field;
        this.from = from;
        this.to = to;
        this.prefix = prefix;
    }

    @Override
    public String toQuery() {
        String fromString = (from == null) ? "*" : (prefix ? (from + "*") : from);
        String toString = (to == null) ? "*" : (prefix ? (to + "*") : to);
        return ((from == null) && (to == null)) ? "" : String.format("%s:[%s TO %s]", field, fromString, toString);
    }

}
