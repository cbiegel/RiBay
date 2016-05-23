package com.ribay.server.repository;

import com.basho.riak.client.api.commands.datatypes.*;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.crdt.types.RiakDatatype;
import com.basho.riak.client.core.query.crdt.types.RiakMap;
import com.basho.riak.client.core.util.BinaryValue;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.ArticleForCart;
import com.ribay.server.material.ArticleForCartWithQ;
import com.ribay.server.material.Cart;
import com.ribay.server.util.JSONUtil;
import com.ribay.server.util.RibayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CartRepository {

    private static final String SESSION_KEY_CART = "cart";

    @Autowired
    private RibayProperties properties;

    @Autowired
    private MyRiakClient client;

    public Cart getCart(final String sessionId) throws Exception {
        Namespace bucket = properties.getBucketSession();
        String key = sessionId;
        Location location = new Location(bucket, key);

        FetchMap command = new FetchMap.Builder(location).build();
        FetchMap.Response response = client.execute(command);

        RiakMap responseFromDB = response.getDatatype().getMap(SESSION_KEY_CART);
        return getCartFromRiakMap(responseFromDB);
    }

    public void changeArticleAmount(String sessionId, ArticleForCart article, int delta) throws Exception {
        Namespace bucket = properties.getBucketSession();
        String key = sessionId;
        Location location = new Location(bucket, key);

        String articleAsString = JSONUtil.write(article);
        MapUpdate update = new MapUpdate().update(SESSION_KEY_CART, new MapUpdate().update(articleAsString, new CounterUpdate(delta)));

        UpdateMap command = new UpdateMap.Builder(location, update).build();
        client.execute(command);
    }

    public Cart removeArticle(String sessionId, ArticleForCart article) throws Exception {
        Namespace bucket = properties.getBucketSession();
        String key = sessionId;
        Location location = new Location(bucket, key);

        FetchMap fetchCommand = new FetchMap.Builder(location).build();
        FetchMap.Response fetchResponse = client.execute(fetchCommand);
        RiakMap responseFromDB = fetchResponse.getDatatype().getMap(SESSION_KEY_CART);
        Cart oldCart = getCartFromRiakMap(responseFromDB); // this is the cart before deleting the article
        Context ctx = fetchResponse.getContext(); // needs context when deleting from set or map

        String articleAsString = JSONUtil.write(article);
        MapUpdate update = new MapUpdate().update(SESSION_KEY_CART, new MapUpdate().removeCounter(articleAsString)); // remove article from cart in db

        UpdateMap updateCommand = new UpdateMap.Builder(location, update).withContext(ctx).build();
        client.execute(updateCommand);

        List<ArticleForCartWithQ> newArticles = oldCart.getArticles().stream() //
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

            List<ArticleForCartWithQ> cartAsList = map.entrySet().stream() //
                    .filter((entry) -> entry.getValue().get(0).getAsCounter().view() > 0) // only articles with a natural number as quantity
                    .map((entry) -> { //
                        ArticleForCart fromDB = JSONUtil.read(entry.getKey().getValue(), ArticleForCart.class);
                        int quantity = entry.getValue().get(0).getAsCounter().view().intValue();

                        ArticleForCartWithQ forCart = new ArticleForCartWithQ();
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

}