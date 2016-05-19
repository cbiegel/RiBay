package com.ribay.server.repository.query.element;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryElementAnd implements QueryElement {

    private List<QueryElement> clauses;

    public QueryElementAnd(QueryElement... clauses) {
        this((clauses == null) ? new ArrayList<>() : Arrays.asList(clauses));
    }

    public QueryElementAnd(Collection<QueryElement> clauses) {
        this.clauses = new ArrayList<>(clauses);
    }

    public void addClause(QueryElement clause) {
        clauses.add(clause);
    }

    @Override
    public String toQuery() {
        List<String> subQueries = clauses.stream().map(QueryElement::toQuery).filter((string) -> !string.isEmpty()).collect(Collectors.toList());
        return subQueries.isEmpty() ? "" : StringUtils.collectionToDelimitedString(subQueries, " AND ");
    }
}
