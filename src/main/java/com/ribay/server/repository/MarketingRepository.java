package com.ribay.server.repository;

import com.basho.riak.client.api.commands.datatypes.*;
import com.basho.riak.client.core.operations.DeleteOperation;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.crdt.types.RiakDatatype;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.basho.riak.client.core.util.BinaryValue;
import com.google.common.primitives.Longs;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.ArticleForCart;
import com.ribay.server.material.ArticleForLastVisited;
import com.ribay.server.material.ArticleShort;
import com.ribay.server.util.JSONUtil;
import com.ribay.server.util.RibayProperties;
import com.ribay.server.util.clock.RibayClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by CD on 24.05.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MarketingRepository {

    @Autowired
    private RibayClock clock;

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    public Future<?> addVisitedArticle(String sessionId, ArticleShort article) throws Exception {
        Namespace bucket = properties.getBucketVisitedArticles();
        String key = sessionId;

        long time = clock.getTime();

        String articleAsString = JSONUtil.write(article);
        byte[] timeAsBytes = Longs.toByteArray(time);

        MapUpdate update = new MapUpdate().update(articleAsString, new RegisterUpdate(timeAsBytes));
        UpdateMap command = new UpdateMap.Builder(new Location(bucket, key), update).build();
        return client.executeAsync(command);
    }

    public List<ArticleForLastVisited> getLastVisitedArticles(String sessionId, int amount) throws Exception {
        Namespace bucket = properties.getBucketVisitedArticles();
        String key = sessionId;

        FetchMap command = new FetchMap.Builder(new Location(bucket, key)).build();
        FetchMap.Response response = client.execute(command);

        RiakMap responseFromDB = response.getDatatype();
        if (responseFromDB == null) {
            return new ArrayList<>();
        } else {
            Map<BinaryValue, List<RiakDatatype>> map = responseFromDB.view();

            List<ArticleForLastVisited> historyAsList = map.entrySet().stream() //
                    .map((entry) -> { //
                        ArticleShort fromDB = JSONUtil.read(entry.getKey().getValue(), ArticleShort.class);
                        long lastVisited = Longs.fromByteArray(entry.getValue().get(0).getAsRegister().view().getValue());

                        ArticleForLastVisited forHistory = new ArticleForLastVisited();
                        forHistory.setId(fromDB.getId());
                        forHistory.setName(fromDB.getName());
                        forHistory.setImage(fromDB.getImage());
                        forHistory.setPrice(fromDB.getPrice());
                        forHistory.setLastVisited(lastVisited);
                        return forHistory;
                    }) //
                    .sorted(Comparator.comparing(ArticleForLastVisited::getLastVisited).reversed()) // sort by timestamp of last visit. higher timestamp comes before lower timestamp
                    .limit(amount) // limit result list as specified
                    .collect(Collectors.toList());
            return historyAsList;
        }
    }

    public Future<?> deleteLastVisitedArticles(String sessionId) throws Exception {
        Namespace bucket = properties.getBucketVisitedArticles();
        String key = sessionId;

        DeleteOperation command = new DeleteOperation.Builder(new Location(bucket, key)).build();
        return client.execute(command);
    }

}
