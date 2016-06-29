package com.ribay.server.repository;

import com.basho.riak.client.api.commands.datatypes.*;
import com.basho.riak.client.core.operations.DeleteOperation;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.crdt.types.RiakDatatype;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.basho.riak.client.core.util.BinaryValue;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.*;
import com.ribay.server.repository.query.QueryBuilderOrder;
import com.ribay.server.util.JSONUtil;
import com.ribay.server.util.RibayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CartRepository {

    @Autowired
    private RibayProperties properties;

    @Autowired
    private MyRiakClient client;

    public Cart getCart(final String sessionId) throws Exception {
        Namespace bucket = properties.getBucketSessionCart();
        String key = sessionId;
        Location location = new Location(bucket, key);

        FetchMap command = new FetchMap.Builder(location).build();
        FetchMap.Response response = client.execute(command);

        RiakMap responseFromDB = response.getDatatype();
        return getCartFromRiakMap(responseFromDB);
    }

    public void changeArticleAmount(String sessionId, ArticleShort article, int delta) throws Exception {
        Namespace bucket = properties.getBucketSessionCart();
        String key = sessionId;
        Location location = new Location(bucket, key);

        String articleAsString = JSONUtil.write(article);
        MapUpdate update = new MapUpdate().update(articleAsString, new CounterUpdate(delta));

        UpdateMap command = new UpdateMap.Builder(location, update).build();
        client.execute(command);
    }

    public Cart removeArticle(String sessionId, ArticleShort article) throws Exception {
        Namespace bucket = properties.getBucketSessionCart();
        String key = sessionId;
        Location location = new Location(bucket, key);

        FetchMap fetchCommand = new FetchMap.Builder(location).build();
        FetchMap.Response fetchResponse = client.execute(fetchCommand);

        RiakMap responseFromDB = fetchResponse.getDatatype();
        Cart oldCart = getCartFromRiakMap(responseFromDB); // this is the cart before deleting the article
        Context ctx = fetchResponse.getContext(); // needs context when deleting from set or map

        String articleAsString = JSONUtil.write(article);
        MapUpdate update = new MapUpdate().removeCounter(articleAsString); // remove article from cart in db

        UpdateMap updateCommand = new UpdateMap.Builder(location, update).withContext(ctx).build();
        client.execute(updateCommand);

        List<ArticleForCart> newArticles = oldCart.getArticles().stream() //
                .filter((articleInCart) -> !articleInCart.getId().equals(article.getId())) // remove article from cart for client result
                .collect(Collectors.toList()); //
        Cart newCart = new Cart(newArticles);
        return newCart;
    }

    private Cart getCartFromRiakMap(RiakMap responseFromDB) {
        if (responseFromDB == null) {
            return new Cart();
        } else {
            Map<BinaryValue, List<RiakDatatype>> map = responseFromDB.view();

            List<ArticleForCart> cartAsList = map.entrySet().stream() //
                    .filter((entry) -> entry.getValue().get(0).getAsCounter().view() > 0) // only articles with a natural number as quantity
                    .map((entry) -> { //
                        ArticleShort fromDB = JSONUtil.read(entry.getKey().getValue(), ArticleShort.class);
                        int quantity = entry.getValue().get(0).getAsCounter().view().intValue();

                        ArticleForCart forCart = new ArticleForCart();
                        forCart.setId(fromDB.getId());
                        forCart.setName(fromDB.getName());
                        forCart.setImage(fromDB.getImage());
                        forCart.setPrice(fromDB.getPrice());
                        forCart.setQuantity(quantity);
                        return forCart;
                    }) //
                    .collect(Collectors.toList());

            return new Cart(cartAsList);
        }
    }

    public Future<?> deleteCart(String sessionId) throws Exception {
        Namespace bucket = properties.getBucketSessionCart();
        String key = sessionId;

        DeleteOperation command = new DeleteOperation.Builder(new Location(bucket, key)).build();
        return client.execute(command);
    }

    public void storeFinishedOrder(OrderFinished order) throws Exception {
        // TODO: store order
        // use a fact for each order because number of orders can be really high
        // this also prevents duplicate orders when using the order id as key
        // add search index so trader can query all orders and users can query their orders using filters
    }

    public List<OrderFinished> getAllOrders(PageInfo pageInfo) throws Exception {
        OrderQuery allOrderQuery = new OrderQuery();
        allOrderQuery.setUserId(null); // show orders of all users
        allOrderQuery.setDateFrom(null); // do not filter by date
        allOrderQuery.setDateTo(null); // do not filter by date
        allOrderQuery.setPageInfo(pageInfo);
        return queryOrders(allOrderQuery);
    }

    public List<OrderFinished> getUserOrders(String userId, PageInfo pageInfo) throws Exception {
        OrderQuery allOrderQuery = new OrderQuery();
        allOrderQuery.setUserId(userId); // show orders of specified user
        allOrderQuery.setDateFrom(null); // do not filter by date
        allOrderQuery.setDateTo(null); // do not filter by date
        allOrderQuery.setPageInfo(pageInfo);
        return queryOrders(allOrderQuery);
    }

    private List<OrderFinished> queryOrders(OrderQuery orderQuery) throws Exception {
        String query = new QueryBuilderOrder().buildQuery(orderQuery);
        PageInfo pageInfo = orderQuery.getPageInfo();
        // TODO implement
        return new ArrayList<>();
    }


}