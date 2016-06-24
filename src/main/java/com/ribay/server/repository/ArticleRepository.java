package com.ribay.server.repository;

import com.basho.riak.client.api.commands.datatypes.FetchMap;
import com.basho.riak.client.api.commands.indexes.BinIndexQuery;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.operations.SearchOperation;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.crdt.types.RiakCounter;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.basho.riak.client.core.query.crdt.types.RiakRegister;
import com.basho.riak.client.core.query.indexes.StringBinIndex;
import com.basho.riak.client.core.util.BinaryValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.exception.NotFoundException;
import com.ribay.server.material.*;
import com.ribay.server.material.continuation.ArticleReviewsContinuation;
import com.ribay.server.repository.query.QueryBuilder;
import com.ribay.server.repository.query.QueryBuilderArticle;
import com.ribay.server.util.RibayProperties;
import com.ribay.server.util.riak.search.RiakSearchHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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

    @Autowired
    private RiakSearchHelper riakSearchHelper;

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

        LOGGER.debug("query: " + command.getQueryInfo().toStringUtf8());

        client.execute(command);

        SearchOperation.Response response = command.get();
        List<Map<String, List<String>>> searchResults = response.getAllResults();

        LOGGER.debug("result: " + searchResults);

        // map solr result document to object
        List<ArticleForSearch> result = searchResults.stream().map((map) -> {
            ArticleForSearch item = new ArticleForSearch();
            item.setId(riakSearchHelper.getString("id_register", map));
            item.setTitle(riakSearchHelper.getString("title_register", map));
            item.setYear(riakSearchHelper.getString("year_register", map));
            item.setVotes(riakSearchHelper.getInteger("votes_counter", map));
            item.setSumRatings(riakSearchHelper.getInteger("sumRatings_counter", map));
            item.setImage(riakSearchHelper.getString("image_register", map));
            item.setMovie(riakSearchHelper.getBoolean("isMovie_flag", map));
            item.setPrice(riakSearchHelper.getInteger("price_register", map));
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

    public ArticleDynamic getArticleInformationDynamic(final String articleId) throws Exception
    {
        Namespace bucket = properties.getBucketArticlesDynamic();
        String key = articleId;
        Location location = new Location(bucket, key);

        FetchMap command = new FetchMap.Builder(location).build();
        FetchMap.Response response = client.execute(command);

        RiakMap responseFromDB = response.getDatatype();
        return getArticleDynamicFromRiakMap(responseFromDB);
    }

    private ArticleDynamic getArticleDynamicFromRiakMap(RiakMap responseFromDB) {
        RiakRegister priceRegister = responseFromDB.getRegister("price");
        RiakCounter stockCounter = responseFromDB.getCounter("stock");
        RiakCounter sumRatingsCounter = responseFromDB.getCounter("sumRatings");
        RiakCounter countRatingsCounter = responseFromDB.getCounter("countRatings");

        int price = new BigInteger(priceRegister.getValue().getValue()).intValue();

    }

    public ArticleReviewsContinuation getReviewsForArticle(String articleId, String continuation) throws Exception {
        String bucket = properties.getBucketArticleReviews() + articleId;
        Namespace namespace = new Namespace(bucket);

        // TODO: allow client to choose range of ratings
        BinIndexQuery biq = new BinIndexQuery.Builder(namespace, "index_rating", "0", "5")
                .withMaxResults(10)
                .withPaginationSort(true)
                .withContinuation((continuation == null) ? null : BinaryValue.create(continuation))
                .build();

        BinIndexQuery.Response response = client.execute(biq);

        ArticleReviewsContinuation resultValue = new ArticleReviewsContinuation();

        List<ArticleReview> reviews = response.getEntries().stream() //
                .parallel() // parallel for faster fetching
                .map(BinIndexQuery.Response.Entry::getRiakObjectLocation) // get location
                .map((location) -> {
                    FetchValue fetchValue = new FetchValue.Builder(location).build();
                    try {
                        FetchValue.Response result = client.execute(fetchValue);
                        return result.getValue(ArticleReview.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }) // get value for location
                .collect(Collectors.toList());
        resultValue.setReviews(reviews);

        if (response.hasContinuation()) {
            String newContinuation = response.getContinuation().toStringUtf8();
            resultValue.setContinuation(newContinuation);
        }

        return resultValue;
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

    public ArticleReview iSFirstReviewForArticle(String articleId, String uuid) throws Exception {

        // "article_reviews_<articleId>"
        String bucket = properties.getBucketArticleReviews() + articleId;
        Namespace namespace = new Namespace(bucket);

        Location location = new Location(new Namespace(bucket), uuid);
        FetchValue fetchOp = new FetchValue.Builder(location).build();
        FetchValue.Response fetchResp = client.execute(fetchOp);

        if(fetchResp.isNotFound()) {
            throw new NotFoundException();
        } else {
            return fetchResp.getValue(ArticleReview.class);
        }
    }
}
