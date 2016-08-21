package com.ribay.server.repository;

import com.basho.riak.client.api.commands.datatypes.*;
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
import com.google.common.util.concurrent.Futures;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.exception.NotFoundException;
import com.ribay.server.material.*;
import com.ribay.server.material.continuation.ArticleReviewsContinuation;
import com.ribay.server.repository.query.QueryBuilder;
import com.ribay.server.repository.query.QueryBuilderArticle;
import com.ribay.server.repository.query.element.QueryElementOr;
import com.ribay.server.repository.query.element.QueryElementStartsWith;
import com.ribay.server.util.RibayConstants;
import com.ribay.server.util.RibayProperties;
import com.ribay.server.util.riak.RiakObjectBuilder;
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

    public static final String PRICE_CRDT_NAME = "price";
    public static final String STOCK_CRDT_NAME = "stock";
    public static final String SUM_RATINGS_CRDT_NAME = "sumRatings";
    public static final String COUNT_RATINGS_CRDT_NAME = "countRatings";

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
            item.setVotes(riakSearchHelper.getInteger("countRatings_counter", map));
            item.setSumRatings(riakSearchHelper.getInteger("sumRatings_counter", map));
            item.setImage(riakSearchHelper.getString("image_register", map));
            item.setMovie(riakSearchHelper.getBoolean("isMovie_flag", map));
            item.setPrice(riakSearchHelper.getInteger("price_register", map));
            return item;
        }).collect(Collectors.toList());

        int numResults = response.numResults();

        return new ArticleSearchResult(result, numResults);
    }

    public List<ArticleShortest> getArticleTypeahead(String text) throws Exception {

        QueryElementStartsWith idFilter = new QueryElementStartsWith("id", text);
        QueryElementStartsWith titleFilter = new QueryElementStartsWith("title", text);
        // QueryElementStartsWith actorsFilter = new QueryElementStartsWith("actors", text);
        QueryElementOr textFilter = new QueryElementOr(idFilter, titleFilter); // text must be in either id or title

        String queryString = textFilter.toQuery();

        SearchOperation command = new SearchOperation.Builder(BinaryValue.create("article_typeahead"), queryString) //
                .withNumRows(10) //
                .build();

        SearchOperation.Response response = client.execute(command).get();

        List<Map<String, List<String>>> searchResults = response.getAllResults();

        // map solr result document to object
        List<ArticleShortest> result = searchResults.stream().map((map) -> {
            ArticleShortest item = new ArticleShortest();
            item.setId(riakSearchHelper.getString("id", map));
            item.setName(riakSearchHelper.getString("title", map));
            item.setImage(riakSearchHelper.getString("imageId", map));
            return item;
        }).collect(Collectors.toList());

        return result;
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

    public ArticleDynamic getArticleInformationDynamic(final String articleId) throws Exception {
        Namespace bucket = properties.getBucketArticlesDynamic();
        String key = articleId;
        Location location = new Location(bucket, key);

        FetchMap command = new FetchMap.Builder(location).build();
        FetchMap.Response response = client.execute(command);

        RiakMap responseFromDB = response.getDatatype();
        return getArticleDynamicFromRiakMap(responseFromDB);
    }

    private ArticleDynamic getArticleDynamicFromRiakMap(RiakMap responseFromDB) {
        RiakRegister priceRegister = responseFromDB.getRegister(PRICE_CRDT_NAME);
        RiakCounter stockCounter = responseFromDB.getCounter(STOCK_CRDT_NAME);
        RiakCounter sumRatingsCounter = responseFromDB.getCounter(SUM_RATINGS_CRDT_NAME);
        RiakCounter countRatingsCounter = responseFromDB.getCounter(COUNT_RATINGS_CRDT_NAME);

        int price = (priceRegister == null) ? 0 : new BigInteger(priceRegister.getValue().getValue()).intValue();
        int stock = (stockCounter == null) ? 0 : stockCounter.view().intValue();
        int sumRatings = (sumRatingsCounter == null) ? 0 : sumRatingsCounter.view().intValue();
        int countRatings = (countRatingsCounter == null) ? 0 : countRatingsCounter.view().intValue();

        ArticleDynamic result = new ArticleDynamic();
        result.setPrice(price);
        result.setStock(stock);
        result.setSumRatings(sumRatings);
        result.setCountRatings(countRatings);
        return result;
    }

    public ArticleShort getArticleShort(final String articleId) throws Exception {
        // articleShort data (id, title, image, price) can be fetched at once using the search bucket

        Namespace bucket = properties.getBucketArticlesSearch();
        String key = articleId;
        Location location = new Location(bucket, key);

        FetchMap fetchCommand = new FetchMap.Builder(location).build();
        FetchMap.Response response = client.execute(fetchCommand);

        RiakMap responseFromDB = response.getDatatype();
        return getArticleShortFromRiakMap(responseFromDB);
    }

    private ArticleShort getArticleShortFromRiakMap(RiakMap responseFromDB) {
        RiakRegister idRegister = responseFromDB.getRegister("id");
        RiakRegister titleRegister = responseFromDB.getRegister("title");
        RiakRegister imageRegister = responseFromDB.getRegister("image");
        RiakRegister priceRegister = responseFromDB.getRegister(PRICE_CRDT_NAME);

        String id = (idRegister == null) ? null : idRegister.getValue().toString();
        String title = (titleRegister == null) ? null : titleRegister.getValue().toString();
        String image = (imageRegister == null) ? null : imageRegister.getValue().toString();
        int price = (priceRegister == null) ? 0 : Integer.parseInt(priceRegister.getValue().toString());

        ArticleShort result = new ArticleShort();
        result.setId(id);
        result.setName(title);
        result.setImage(image);
        result.setPrice(price);
        return result;
    }

    public ArticleReviewsContinuation getReviewsForArticle(String articleId, String continuation, RatingFilterRange ratingRange) throws Exception {
        String bucket = properties.getBucketArticleReviews() + articleId;
        Namespace namespace = new Namespace(bucket);

        String ratingFrom = ratingRange == null ? "0" : ratingRange.getRatingFrom();
        String ratingTo = ratingRange == null ? "5" : ratingRange.getRatingTo();

        BinIndexQuery biq = new BinIndexQuery.Builder(namespace, "index_rating", ratingFrom, ratingTo)
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

    public void submitArticleReview(ArticleReview review, String uuid, ArticleReview previousReview) throws Exception {
        long reviewRatingDelta;
        long countRatingDelta;

        submitReviewData(review, uuid);

        if (previousReview != null) {
            // A previous review exists. The review was edited. Calculate the delta of the ratings
            reviewRatingDelta = Long.valueOf(review.getRatingValue()) - Long.valueOf(previousReview.getRatingValue());
            countRatingDelta = 0;
        } else {
            // A new review does not need to calculate a delta, because there is no previous review
            reviewRatingDelta = Long.valueOf(review.getRatingValue());
            countRatingDelta = 1;
        }

        // TODO make async?
        updateReviewCRDTs(review.getArticleId(), reviewRatingDelta, countRatingDelta);
    }

    private void submitReviewData(ArticleReview review, String uuid) throws Exception {
        // "article_reviews_<articleId>"
        String bucket = properties.getBucketArticleReviews() + review.getArticleId();
        Location location = new Location(new Namespace(bucket), uuid);

        RiakObject riakObj = new RiakObjectBuilder(review) //
                .withIndex(StringBinIndex.named("index_date"), review.getDate()) //
                .withIndex(StringBinIndex.named("index_rating"), review.getRatingValue()) //
                .build(); //
        StoreValue storeOp = new StoreValue.Builder(riakObj).withLocation(location).build();

        client.execute(storeOp);
    }

    private void updateReviewCRDTs(String articleId, long deltaSumRatings, long deltaCountRatings) throws Exception {
        Future<?> f1 = updateReviewCRDTsForDynamic(articleId, deltaSumRatings, deltaCountRatings);
        Future<?> f2 = updateReviewCRDTsForSearch(articleId, deltaSumRatings, deltaCountRatings);

        f1.get();
        f2.get();
    }

    private Future<?> updateReviewCRDTsForDynamic(String articleId, long deltaSumRatings, long deltaCountRatings) throws Exception {
        Namespace bucket = properties.getBucketArticlesDynamic();
        String key = articleId;
        Location location = new Location(bucket, key);

        // Update both rating CRDTS: sumRatings for the sum of all rating, countRatings for the count of ratings
        // Increment count of ratings, update sum of ratings (can also be decremented)
        MapUpdate update = new MapUpdate() //
                .update(SUM_RATINGS_CRDT_NAME, new CounterUpdate(deltaSumRatings)) //
                .update(COUNT_RATINGS_CRDT_NAME, new CounterUpdate(deltaCountRatings)); //
        UpdateMap command = new UpdateMap.Builder(location, update).build();
        return client.executeAsync(command);
    }

    private Future<?> updateReviewCRDTsForSearch(String articleId, long deltaSumRatings, long deltaCountRatings) throws Exception {
        Namespace bucket = properties.getBucketArticlesSearch();
        String key = articleId;
        Location location = new Location(bucket, key);

        // Update both rating CRDTS: sumRatings for the sum of all rating, countRatings for the count of ratings
        // Increment count of ratings, update sum of ratings (can also be decremented)
        MapUpdate update = new MapUpdate() //
                .update(SUM_RATINGS_CRDT_NAME, new CounterUpdate(deltaSumRatings)) //
                .update(COUNT_RATINGS_CRDT_NAME, new CounterUpdate(deltaCountRatings)); //
        UpdateMap command = new UpdateMap.Builder(location, update).build();
        return client.executeAsync(command);
    }

    public ArticleReview iSFirstReviewForArticle(String articleId, String uuid) throws Exception {

        // "article_reviews_<articleId>"
        String bucket = properties.getBucketArticleReviews() + articleId;

        Location location = new Location(new Namespace(bucket), uuid);
        FetchValue fetchOp = new FetchValue.Builder(location).build();
        FetchValue.Response fetchResp = client.execute(fetchOp);

        if (fetchResp.isNotFound()) {
            // no review exists for the given article
            return null;
        } else {
            return fetchResp.getValue(ArticleReview.class);
        }
    }

    public int getStock(String articleId) throws Exception {
        // fetch dynamic article values
        Namespace bucket = properties.getBucketArticlesDynamic();
        String key = articleId;
        Location location = new Location(bucket, key);

        FetchMap fetchCommand = new FetchMap.Builder(location).build();
        FetchMap.Response fetchResponse = client.execute(fetchCommand);
        RiakMap map = fetchResponse.getDatatype();

        ArticleDynamic articleDynamic = getArticleDynamicFromRiakMap(map);
        return articleDynamic.getStock();
    }

    public void changeStock(String articleId, int diff) throws Exception {
        Future<?> f1 = changeStockForDynamic(articleId, diff);
        Future<?> f2 = changeStockForSearch(articleId, diff);

        f1.get();
        f2.get();
    }

    public Future<?> changeStocks(Map<String, Integer> articleIdToDiff) throws Exception {
        for (Map.Entry<String, Integer> entry : articleIdToDiff.entrySet()) {
            String articleId = entry.getKey();
            int diff = entry.getValue();

            changeStockForDynamic(articleId, diff);
            changeStockForSearch(articleId, diff);
        }
        return Futures.immediateFuture(null);
    }

    private Future<?> changeStockForDynamic(String articleId, int diff) throws Exception {
        Namespace bucket = properties.getBucketArticlesDynamic();
        String key = articleId;
        Location location = new Location(bucket, key);

        MapUpdate update = new MapUpdate().update(STOCK_CRDT_NAME, new CounterUpdate(diff));
        UpdateMap command = new UpdateMap.Builder(location, update).build();
        return client.executeAsync(command);
    }

    private Future<?> changeStockForSearch(String articleId, int diff) throws Exception {
        Namespace bucket = properties.getBucketArticlesSearch();
        String key = articleId;
        Location location = new Location(bucket, key);

        MapUpdate update = new MapUpdate().update(STOCK_CRDT_NAME, new CounterUpdate(diff));
        UpdateMap command = new UpdateMap.Builder(location, update).build();
        return client.executeAsync(command);
    }

    public void setPrice(String articleId, int price) throws Exception {
        // set price in dynamic bucket and in search bucket
        Future<?> f1 = setPriceForDynamic(articleId, price);
        Future<?> f2 = setPriceForSearch(articleId, price);

        f1.get();
        f2.get();
    }

    private Future<?> setPriceForDynamic(String articleId, int price) throws Exception {
        Namespace bucket = properties.getBucketArticlesDynamic();
        String key = articleId;
        Location location = new Location(bucket, key);

        MapUpdate update = new MapUpdate().update(PRICE_CRDT_NAME, new RegisterUpdate(BigInteger.valueOf(price).toByteArray()));
        UpdateMap command = new UpdateMap.Builder(location, update).build();
        return client.executeAsync(command);
    }

    private Future<?> setPriceForSearch(String articleId, int price) throws Exception {
        Namespace bucket = properties.getBucketArticlesSearch();
        String key = articleId;
        Location location = new Location(bucket, key);

        MapUpdate update = new MapUpdate().update(PRICE_CRDT_NAME, new RegisterUpdate(String.format(RibayConstants.NUMBERFORMAT_PRICE_SEARCH, price)));
        UpdateMap command = new UpdateMap.Builder(location, update).build();
        return client.executeAsync(command);
    }

}
