package com.ribay.server.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

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

        SearchOperation command = new SearchOperation.Builder(BinaryValue.create("schema_article"), queryString) //
                .withStart(start) //
                .withNumRows(pageSize) //
                .build();

        client.execute(command);

        SearchOperation.Response response = command.get();
        List<Map<String, List<String>>> results = response.getAllResults();
        // TODO convert result
        return null;
    }
    
    public Article getArticleInformation(final String articleId) throws Exception {
	String bucket = properties.getBucketArticles();
        Location location = new Location(new Namespace(bucket), articleId);
        FetchValue fetchOp = new FetchValue.Builder(location).build();
        return client.execute(fetchOp).getValue(Article.class);
    }

}
