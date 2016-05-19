package com.ribay.server.repository.query.element;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CD on 19.05.2016.
 */
public class QueryElementMatchOne implements QueryElement {

    private final String field;
    private final Collection<String> values;

    public QueryElementMatchOne(String field, Collection<String> values) {
        this.field = field;
        this.values = values;
    }

    @Override
    public String toQuery() {
        // match all is like one of the given values must be in that multi-value field
        List<QueryElement> matches = values.stream() //
                .map((value) -> new QueryElementEqual(field, value)) //
                .collect(Collectors.toList()); //
        return matches.isEmpty() ? "" : new QueryElementOr(matches).toQuery();
    }
}
