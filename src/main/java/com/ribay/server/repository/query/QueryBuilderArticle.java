package com.ribay.server.repository.query;

import com.ribay.server.material.ArticleQuery;
import com.ribay.server.repository.query.element.*;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryBuilderArticle implements QueryBuilder<ArticleQuery> {


    @Override
    public String buildQuery(ArticleQuery query) {
        QueryElementStartsWith titleFilter = new QueryElementStartsWith("title_register", query.getText());
        QueryElementStartsWith actorsFilter = new QueryElementStartsWith("actors_set", query.getText());
        QueryElementOr textFilter = new QueryElementOr(titleFilter, actorsFilter); // text must be in either title or actors

        // TODO filter genre
        QueryElementBool movieFilter = new QueryElementBool("isMovie_flag", query.isMovie());
        // TODO enable
        // QueryElementRange priceFilter = new QueryElementRange("price_register", query.getPrice_low(), query.getPrice_high());
        // QueryElementRange ratingFilter = new QueryElementRange("rating", query.getRating_low(), query.getRating_high());
        QueryElementRange votesFilter = new QueryElementRange("votes_counter", query.getVotes_low(), query.getVotes_high());
        QueryElementAnd fullFilter = new QueryElementAnd(textFilter, movieFilter, /* priceFilter, ratingFilter, */ votesFilter);
        return fullFilter.toQuery();
    }

}
