package com.ribay.server.repository;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.basho.riak.client.api.commands.indexes.BinIndexQuery;
import com.basho.riak.client.api.convert.ConversionException;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.StringBinIndex;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ribay.server.exception.NotFoundException;
import com.ribay.server.material.*;
import com.ribay.server.material.continuation.ArticleReviewsContinuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.operations.SearchOperation;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.util.BinaryValue;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.repository.query.QueryBuilder;
import com.ribay.server.repository.query.QueryBuilderArticle;
import com.ribay.server.util.RibayProperties;

import javax.websocket.Decoder;

/**
 * Created by CD on 30.04.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ArticleRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleRepository.class);

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    public Future<?> storeArticle(Article article) throws Exception {
        String bucket = properties.getBucketArticles();
        String key = article.getId();

        Location location = new Location(new Namespace(bucket), key);
        StoreValue storeOp = new StoreValue.Builder(article).withLocation(location).build();
        return client.executeAsync(storeOp);
    }

    public ArticleSearchResult queryArticles(ArticleQuery query) throws Exception {
        QueryBuilder<ArticleQuery> queryBuilder = new QueryBuilderArticle();
        String queryString = queryBuilder.buildQuery(query);

        PageInfo pageInfo = query.getPageInfo();
        int start = pageInfo.getStart();
        int pageSize = pageInfo.getPage_size();

        SearchOperation command = new SearchOperation.Builder(BinaryValue.create("articles"), queryString) //
                .withStart(start) //
                .withNumRows(pageSize) //
                .withPresort("key") // to get the same results when filtering with same settings
                .build();
        // TODO specify sort field and order

        LOGGER.info("query: " + command.getQueryInfo().toStringUtf8());

        client.execute(command);

        SearchOperation.Response response = command.get();
        List<Map<String, List<String>>> searchResults = response.getAllResults();

        LOGGER.info("result: " + searchResults);

        // map solr result document to object
        List<ArticleForSearch> result = searchResults.stream().map((map) -> {
            ArticleForSearch item = new ArticleForSearch();
            item.setId(map.getOrDefault("id_register", Collections.singletonList(null)).get(0));
            item.setTitle(map.getOrDefault("title_register", Collections.singletonList(null)).get(0));
            item.setYear(map.getOrDefault("year_register", Collections.singletonList(null)).get(0));
            item.setVotes(Integer.valueOf(map.getOrDefault("votes_counter", Collections.singletonList("0")).get(0)));
            item.setSumRatings(Integer.valueOf(map.getOrDefault("sumRatings_counter", Collections.singletonList("0")).get(0)));
            item.setImage(map.getOrDefault("image_register", Collections.singletonList(null)).get(0));
            item.setMovie(Boolean.valueOf(map.getOrDefault("isMovie_flag", Collections.singletonList("false")).get(0)));
            item.setPrice(Integer.valueOf(map.getOrDefault("price_register", Collections.singletonList("0")).get(0)));
            return item;
        }).collect(Collectors.toList());

        int numResults = response.numResults();

        return new ArticleSearchResult(result, numResults);
    }

    public Article getArticleInformation(final String articleId) throws Exception {
        String bucket = properties.getBucketArticles();
        String key = articleId;
        Location location = new Location(new Namespace(bucket), key);
        FetchValue fetchOp = new FetchValue.Builder(location).build();
        FetchValue.Response fetchResp = client.execute(fetchOp);
        if (fetchResp.isNotFound()) {
            throw new NotFoundException();
        } else {
            return fetchResp.getValue(Article.class);
        }
    }

    public ArticleReviewsContinuation getReviewsForArticle(String articleId, String uuid) throws Exception {
        String bucket = properties.getBucketArticleReviews() + articleId;
        Namespace namespace = new Namespace(bucket);

        BinIndexQuery biq = new BinIndexQuery.Builder(namespace, "index_rating", "0", "5")
                .withMaxResults(10)
                .withPaginationSort(true)
                .build();
        BinIndexQuery.Response response = client.execute(biq);

        if(!response.hasEntries()) {
            throw new NotFoundException();
        } else {
            ArticleReviewsContinuation resultValue = new ArticleReviewsContinuation();

           List<ArticleReview> reviews =  response.getEntries().stream().parallel().map(BinIndexQuery.Response.Entry::getRiakObjectLocation).map(
                    (location) -> {
                        FetchValue fetchValue = new FetchValue.Builder(location).build();
                        try {
                            FetchValue.Response result = client.execute(fetchValue);
                            return result.getValue(ArticleReview.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }).collect(Collectors.toList());
            resultValue.setReviews(reviews);

            if(response.hasContinuation()) {
                resultValue.setContinuation(response.getContinuation().toString());
            }

            return resultValue;
        }
    }

    public void submitArticleReview(ArticleReview review, String uuid) throws Exception {

        // "article_reviews_<articleId>"
        String bucket = properties.getBucketArticleReviews() + review.getArticleId();
        Location location = new Location(new Namespace(bucket), uuid);

        String jsonString = new ObjectMapper().writeValueAsString(review);
        RiakObject riakObj = new RiakObject();
        riakObj.setContentType("application/json");
        riakObj.setValue(BinaryValue.create(jsonString));
        riakObj.getIndexes().getIndex(StringBinIndex.named("index_date")).add(review.getDate());
        riakObj.getIndexes().getIndex(StringBinIndex.named("index_rating")).add(review.getRatingValue());

        StoreValue storeOp = new StoreValue.Builder(riakObj).withLocation(location).build();

        client.execute(storeOp);
    }
}
