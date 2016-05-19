package com.ribay.server.repository.query;

import com.ribay.server.material.ArticleQuery;
import com.ribay.server.repository.query.element.*;

/**
 * Created by CD on 01.05.2016.
 */
public class QueryBuilderArticle implements QueryBuilder<ArticleQuery> {

    private static final String NUMBERFORMAT_PRICE = "%04d";

    @Override
    public String buildQuery(ArticleQuery query) {
        QueryElementStartsWith titleFilter = new QueryElementStartsWith("title_register", query.getText());
        QueryElementStartsWith actorsFilter = new QueryElementStartsWith("actors_set", query.getText());
        QueryElementOr textFilter = new QueryElementOr(titleFilter, actorsFilter); // text must be in either title or actors

        QueryElementMatchAll genreFilter = new QueryElementMatchAll("genre_set", query.getGenre());
        QueryElementBool movieFilter = new QueryElementBool("isMovie_flag", query.isMovie());
        QueryElementTextRange priceFilter = new QueryElementTextRange("price_register", formatPrice(query.getPrice_low()), formatPrice(query.getPrice_high()), false);
        // TODO enable
        // QueryElementRange ratingFilter = new QueryElementRange("rating", query.getRating_low(), query.getRating_high());
        QueryElementRange votesFilter = new QueryElementRange("votes_counter", query.getVotes_low(), query.getVotes_high());
        QueryElementAnd fullFilter = new QueryElementAnd(textFilter, genreFilter, movieFilter, priceFilter, /* ratingFilter, */ votesFilter);
        if (query.getImageOnly()) {
            fullFilter.addClause(new QueryNotNull("image_register"));
        }
        return fullFilter.toQuery();
    }

    private String formatPrice(Integer value) {
        return (value == null) ? null : String.format(NUMBERFORMAT_PRICE, value);
    }

}
