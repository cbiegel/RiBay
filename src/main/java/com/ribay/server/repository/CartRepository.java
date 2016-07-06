package com.ribay.server.repository;

import com.basho.riak.client.api.commands.datatypes.*;
import com.basho.riak.client.core.operations.DeleteOperation;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.crdt.types.RiakCounter;
import com.basho.riak.client.core.query.crdt.types.RiakDatatype;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.basho.riak.client.core.query.crdt.types.RiakRegister;
import com.basho.riak.client.core.util.BinaryValue;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.*;
import com.ribay.server.material.converter.Converter;
import com.ribay.server.repository.query.QueryBuilderOrder;
import com.ribay.server.util.JSONUtil;
import com.ribay.server.util.RibayConstants;
import com.ribay.server.util.RibayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CartRepository {

    private static final String CRDT_NAME_DATA = "data";
    private static final String CRDT_NAME_PRICE = "price";
    private static final String CRDT_NAME_QUANTITY = "quantity";

    @Autowired
    private RibayProperties properties;

    @Autowired
    private MyRiakClient client;

    @Autowired
    private Converter<ArticleShort, ArticleShortest> converter;

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

        String articleId = article.getId();
        String articleAsString = JSONUtil.write(converter.convert(article));
        int price = article.getPrice();

        MapUpdate update = new MapUpdate() //
                .update(articleId, new MapUpdate() // map articleid to tuple of ...
                        .update(CRDT_NAME_DATA, new RegisterUpdate(articleAsString)) // ... articleShortest ...
                        .update(CRDT_NAME_PRICE, new RegisterUpdate(BigInteger.valueOf(price).toByteArray())) // ... price ...
                        .update(CRDT_NAME_QUANTITY, new CounterUpdate(delta))); // ... and quantity

        UpdateMap command = new UpdateMap.Builder(location, update).build();
        client.execute(command);
    }

    public Cart removeArticle(String sessionId, String articleId) throws Exception {
        Namespace bucket = properties.getBucketSessionCart();
        String key = sessionId;
        Location location = new Location(bucket, key);

        FetchMap fetchCommand = new FetchMap.Builder(location).build();
        FetchMap.Response fetchResponse = client.execute(fetchCommand);

        RiakMap responseFromDB = fetchResponse.getDatatype();
        Context ctx = fetchResponse.getContext(); // needs context when deleting from set or map

        MapUpdate update = new MapUpdate().removeMap(articleId); // delete by articleId -> removes tuple value too
        UpdateMap command = new UpdateMap.Builder(location, update).withContext(ctx).build();
        client.executeAsync(command);

        Cart oldCart = getCartFromRiakMap(responseFromDB);
        List<ArticleForCart> oldCartContent = oldCart.getArticles();
        List<ArticleForCart> newCartContent = oldCartContent.stream() //
                .filter((articleForCart) -> !articleId.equals(articleForCart.getId())) // only collect articles that do not have the id that was removed
                .collect(Collectors.toList()); //
        return new Cart(newCartContent);
    }

    private Cart getCartFromRiakMap(RiakMap responseFromDB) {
        if (responseFromDB == null) {
            return new Cart(); // no entries -> empty cart
        } else {
            Map<BinaryValue, List<RiakDatatype>> map = responseFromDB.view();

            List<ArticleForCart> cartAsList = map.values().stream() //
                    .map(list -> list.get(0).getAsMap()) // we do not need the articleId, only the tuple value
                    .map(riakMap -> {
                        // parse data from riakMap
                        RiakRegister dataRegister = riakMap.getRegister(CRDT_NAME_DATA);
                        RiakRegister priceRegister = riakMap.getRegister(CRDT_NAME_PRICE);
                        RiakCounter quantityCounter = riakMap.getCounter(CRDT_NAME_QUANTITY);

                        ArticleShortest articleShortest = JSONUtil.read(dataRegister.getValue().getValue(), ArticleShortest.class);
                        int price = new BigInteger(priceRegister.getValue().getValue()).intValue();
                        int quantity = quantityCounter.view().intValue();

                        ArticleForCart articleForCart = new ArticleForCart();
                        articleForCart.setId(articleShortest.getId());
                        articleForCart.setName(articleShortest.getName());
                        articleForCart.setImage(articleShortest.getImage());
                        articleForCart.setPrice(price);
                        articleForCart.setQuantity(quantity);
                        return articleForCart;
                    })
                    .filter(articleForCart -> articleForCart.getQuantity() > 0) // only articles in cart with positive quantity
                    .sorted(Comparator.comparing(ArticleForCart::getImage)) // sort by name
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

}