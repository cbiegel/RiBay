package com.ribay.server.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.basho.riak.client.api.convert.ConversionException;
import com.basho.riak.client.core.query.RiakObject;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ribay.server.exception.NotFoundException;
import com.ribay.server.material.ArticleReview;
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
import com.ribay.server.material.Article;
import com.ribay.server.material.ArticleQuery;
import com.ribay.server.material.PageInfo;
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

    public List<Article> queryArticles(ArticleQuery query, PageInfo pageInfo) throws Exception {
        QueryBuilder<ArticleQuery> queryBuilder = new QueryBuilderArticle();
        String queryString = queryBuilder.buildQuery(query);

        LOGGER.info("query: " + queryString);

        int start = pageInfo.getStart();
        int pageSize = pageInfo.getPage_size();

        SearchOperation command = new SearchOperation.Builder(BinaryValue.create("index_article"), queryString) //
                .withStart(start) //
                .withNumRows(pageSize) //
                .build();

        client.execute(command);

        SearchOperation.Response response = command.get();
        List<Map<String, List<String>>> results = response.getAllResults();

        LOGGER.info("result: " + results);
        // TODO convert result
        return null;
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

    public List<ArticleReview> getReviewsForArticle(String articleId) throws Exception {
        String bucket = properties.getBucketArticleReviews() + articleId;
        String key = "1";
        Location location = new Location(new Namespace(bucket), key);
        FetchValue fetchOp = new FetchValue.Builder(location).build();
        FetchValue.Response fetchResp = client.execute(fetchOp);

            // At least one review already exists for this article
            try {
                List<ArticleReview[]> result = fetchResp.getValues(ArticleReview[].class);
                List<ArticleReview> reviews = new ArrayList<>(Arrays.asList(result.get(0)));
                return reviews;
            } catch (Exception e) {
                // Only one review exists...
                List<ArticleReview> result = fetchResp.getValues(ArticleReview.class);
                return result;
            }
        }

    public void submitArticleReview(ArticleReview review) throws Exception {

        // TODO: Check if article reviews needs to be split and put into new key

        // "article_reviews_<articleId>"
        String bucket = properties.getBucketArticleReviews() + review.getArticleId();
        String key = "1";
        Location location = new Location(new Namespace(bucket), key);

        FetchValue fetchOp = new FetchValue.Builder(location).build();
        FetchValue.Response fetchResp = client.execute(fetchOp);
        if (!fetchResp.isNotFound()) {
            // At least one review already exists for this article
            try {
                List<ArticleReview[]> valuesList = fetchResp.getValues(ArticleReview[].class);
                List<ArticleReview> values = new ArrayList<>();
                for (ArticleReview[] value : valuesList) {
                    for (ArticleReview reviewValue : value) {
                        values.add(reviewValue);
                    }
                }
                values.add(review);

                StoreValue storeOp = new StoreValue.Builder(values).withLocation(location).build();
                client.execute(storeOp);
            } catch (Exception e) {
                // Only one review already exists
                List<ArticleReview> values = fetchResp.getValues(ArticleReview.class);
                values.add(review);
                StoreValue storeOp = new StoreValue.Builder(values).withLocation(location).build();
                client.execute(storeOp);
            }
        } else {
            // No reviews exist for this article yet...
            StoreValue storeOp = new StoreValue.Builder(review).withLocation(location).build();
            client.execute(storeOp);
        }

    }

}
