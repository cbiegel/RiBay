package com.ribay.server.repository.query.element;

/**
 * Created by CD on 18.05.2016.
 */
public class QueryNotNull implements QueryElement {

    private final String field;

    public QueryNotNull(String field) {
        this.field = field;
    }

    @Override
    public String toQuery() {
        return String.format("%s:['' TO *]", field);
    }

}
