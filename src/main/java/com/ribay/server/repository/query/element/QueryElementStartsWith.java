package com.ribay.server.repository.query.element;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryElementStartsWith implements QueryElement {

    private String field;
    private String prefix;

    public QueryElementStartsWith(String field, String prefix) {
        this.field = field;
        this.prefix = prefix;
    }

    @Override
    public String toQuery() {
        return field.isEmpty() ? "" : String.format("%s:%s*", field, prefix);
    }
}
