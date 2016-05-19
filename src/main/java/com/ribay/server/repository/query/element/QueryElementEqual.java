package com.ribay.server.repository.query.element;

/**
 * Created by CD on 19.05.2016.
 */
public class QueryElementEqual implements QueryElement {

    private final String field;
    private final String value;

    public QueryElementEqual(String field, String value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public String toQuery() {
        return String.format("%s:(%s)", field, value);
    }

}
