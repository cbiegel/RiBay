package com.ribay.server.repository;

import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.Article;
import com.ribay.server.util.RibayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * Created by CD on 30.04.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ArticleRepository {

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

}
