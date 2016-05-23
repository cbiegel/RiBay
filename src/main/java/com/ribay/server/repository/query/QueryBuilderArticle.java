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
        // TODO check why filtering by medium rating does not work
        // https://cwiki.apache.org/confluence/display/solr/Function+Queries
        // http://134.100.11.158:8098/search/query/articles?q=(title_register:(So*)%20OR%20actors_set:(So*))%20AND%20price_register:[0000%20TO%202000]%20AND%20image_register:[%27%27%20TO%20*]%20AND%20(_val_:[*%20TO%20*]%20_val_:%22div(sumRatings_counter,votes_counter)%22)
        QueryElementRange ratingFilter = new QueryElementRange("_val_", query.getRating_low(), query.getRating_high(), "_val_:\"div(sumRatings_counter,votes_counter)\"");
        QueryElementRange votesFilter = new QueryElementRange("votes_counter", query.getVotes_low(), query.getVotes_high());
        QueryElementAnd fullFilter = new QueryElementAnd(textFilter, genreFilter, movieFilter, priceFilter, ratingFilter, votesFilter);
        if (query.getImageOnly()) {
            fullFilter.addClause(new QueryNotNull("image_register"));
        }
        return fullFilter.toQuery();
    }

    private String formatPrice(Integer value) {
        return (value == null) ? null : String.format(NUMBERFORMAT_PRICE, value);
    }

}
