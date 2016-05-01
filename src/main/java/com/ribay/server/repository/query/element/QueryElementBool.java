package com.ribay.server.repository.query.element;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryElementBool implements QueryElement {

    private final String field;
    private final Boolean value;

    public QueryElementBool(String field, Boolean value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public String toQuery() {
        return (value == null) ? "" : String.format("%s:%s", field, value.toString());
    }
}
