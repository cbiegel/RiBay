package com.ribay.server.service;

import com.google.common.base.Functions;
import com.ribay.server.exception.*;
import com.ribay.server.material.*;
import com.ribay.server.material.converter.Converter;
import com.ribay.server.repository.ArticleRepository;
import com.ribay.server.repository.CartRepository;
import com.ribay.server.repository.OrderRepository;
import com.ribay.server.util.RequestScopeData;
import com.ribay.server.util.RibayConstants;
import com.ribay.server.util.clock.RibayClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntToLongFunction;
import java.util.stream.Collectors;

/**
 * Created by CD on 04.07.2016.
 */
@RestController
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RequestScopeData requestData;

    @Autowired
    private RibayClock clock;

    @Autowired
    private Converter<Order, OrderFinished> orderConverter;

    @RequestMapping(path = "/user/orders", method = RequestMethod.POST)
    public OrderSearchResult getUserOrders(@RequestParam(value = "continuation", required = false) String continuation) throws Exception {
        if (requestData.getUser() == null) {
            // must be logged in
            throw new NotLoggedInException();
        }

        String userId = requestData.getUser().getUuid().toString();
        return orderRepository.getUserOrders(userId, continuation);
    }

    // TODO move to AdminService
    @RequestMapping(path = "/admin/orders", method = RequestMethod.POST)
    public OrderSearchResult getAllOrders(@RequestParam(value = "continuation", required = false) String continuation) throws Exception {
        return orderRepository.getAllOrders(continuation);
    }

    @RequestMapping(path = "/checkout/start", method = RequestMethod.POST)
    public Order startCheckout() throws Exception {
        if (requestData.getUser() == null) {
            // must be logged in
            throw new NotLoggedInException();
        }

        String sessionId = requestData.getSessionId();
        String userId = requestData.getUser().getUuid().toString();

        Cart cart = cartRepository.getCart(sessionId);
        if (cart.getArticles().isEmpty()) {
            // cart must not be empty
            throw new EmptyCartException();
        }

        // refresh data that might have changed after articles were added to cart (especially prices)
        cart = getRefreshedCart(cart);

        // generate id
        String id = UUID.randomUUID().toString();
        long time = clock.getTime();

        Order order = new Order();
        order.setId(id);
        order.setSessionId(sessionId);
        order.setUserId(userId);
        order.setCart(cart);
        order.setAddress(null); // no address yet
        order.setDateStarted(time);
        order.updateHash();
        return order;
    }

    @RequestMapping(path = "/checkout/finish", method = RequestMethod.POST)
    public OrderFinished finishCheckout(@RequestBody Order order) throws Exception {

        // throws exception when order was manipulated by client
        order.checkHash();

        long now = clock.getTime();
        long diffTime = now - order.getDateStarted();

        if (diffTime > RibayConstants.MAX_AGE_OF_UNFINISHED_ORDER_IN_MS) {
            // if order started too long ago
            throw new OrderTooOldException();
        }

        if (requestData.getUser() == null) {
            // must be logged in
            throw new NotLoggedInException();
        }

        String sessionId = requestData.getSessionId();
        String userId = requestData.getUser().getUuid().toString();

        if (!sessionId.equals(order.getSessionId())) {
            // client that started the order must be the same as the client that finishes the order
            throw new IncorrectClientException();
        }
        if (!userId.equals(order.getUserId())) {
            // user that started the order must be the same as the user that finishes the order
            throw new IncorrectUserException();
        }

        Cart cartFromDB = cartRepository.getCart(sessionId);

        if (cartFromDB.getArticles().isEmpty()) {
            // cart must not be empty when finishing order
            throw new EmptyCartException();
        }

        Cart cartFromDBRefreshed = getRefreshedCart(cartFromDB);

        Cart cartFromClient = order.getCart();

        if (hasCartChanged(cartFromDBRefreshed, cartFromClient)) {
            // if cart has changed -> client has to confirm new cart explicitly
            order.setCart(cartFromDBRefreshed); // put actual cart from db into order and let client confirm the new cart
            order.updateHash();
            throw new CartChangedException(order);
        }

        if (order.getAddress() == null) {
            // address is missing
            throw new IncompleteOrderException();
        }

        // TODO more preconditions?

        // ### now everything is alright - finish order ### //

        OrderFinished orderFinished = orderConverter.convert(order);

        cartRepository.deleteCart(sessionId);
        orderRepository.storeFinishedOrder(orderFinished);

        // update stock for each bought article
        Map<String, Integer> articleIdToDiff = orderFinished.getArticles().stream().collect(Collectors.toMap(ArticleForCart::getId, negate(ArticleForCart::getQuantity)));
        articleRepository.changeStocks(articleIdToDiff);

        return orderFinished;
    }

    private boolean hasCartChanged(Cart c1, Cart c2) {
        List<ArticleForCart> a1 = c1.getArticles();
        List<ArticleForCart> a2 = c2.getArticles();
        if (a1.size() != a2.size()) {
            // different size of cart -> at least one article added to or removed from cart
            return true;
        }

        // carts have same size. compare each element in order (articles have to be ordered for this to work)
        for (int idx = 0; idx < a1.size(); idx++) {
            ArticleForCart left = a1.get(idx);
            ArticleForCart right = a2.get(idx);

            if (!left.getId().equals(right.getId())) {
                // different ids -> different article (at least one article added and another one removed from cart)
                return true;
            }
            if (left.getQuantity() != right.getQuantity()) {
                // same article but different quantity -> quantity changed
                return true;
            }
            if (left.getPrice() != right.getPrice()) {
                // same article, same quantity but different price -> price has changed!
                return true;
            }
        }

        // all articles are the same. cart has not been changed
        return false;
    }

    /**
     * Returns a new cart object with the same entries as the specified but with updated prices from db.
     */
    private Cart getRefreshedCart(Cart cart) {
        List<ArticleForCart> refreshedArticles = cart.getArticles().parallelStream().map((articleOld -> {
            ArticleForCart articleNew = new ArticleForCart();
            articleNew.setId(articleOld.getId());
            articleNew.setName(articleOld.getName());
            articleNew.setImage(articleOld.getImage());
            articleNew.setQuantity(articleOld.getQuantity());
            articleNew.setPrice(articleOld.getPrice());

            try {
                ArticleDynamic articleDynamic = articleRepository.getArticleInformationDynamic(articleOld.getId());
                articleNew.setPrice(articleDynamic.getPrice());
            } catch (Exception e) {
                LOGGER.error("error while fetching price for article", e);
            }

            return articleNew;
        })).collect(Collectors.toList());

        Cart refreshedCart = new Cart(refreshedArticles); // use new arraylist here for correct serialization
        // TODO maybe update db?
        return refreshedCart;
    }

    private static <T> Function<T, Integer> negate(Function<T, Integer> input) {
        return input.andThen((value) -> -value);
    }

}
