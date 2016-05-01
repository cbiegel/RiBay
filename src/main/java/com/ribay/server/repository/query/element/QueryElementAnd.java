package com.ribay.server.repository.query.element;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryElementAnd implements QueryElement {

    private List<QueryElement> clauses;

    public QueryElementAnd(QueryElement... clauses) {
        this.clauses = (clauses == null) ? new ArrayList<>() : Arrays.asList(clauses);
    }

    @Override
    public String toQuery() {
        List<String> subQueries = clauses.stream().map(QueryElement::toQuery).filter((string) -> !string.isEmpty()).collect(Collectors.toList());
        return subQueries.isEmpty() ? "" : StringUtils.collectionToDelimitedString(subQueries, " AND ");
    }
}
