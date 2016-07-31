package com.ribay.server.service;

import com.ribay.server.RibayServerApplication;
import com.ribay.server.exception.*;
import com.ribay.server.material.*;
import com.ribay.server.repository.OrderRepository;
import com.ribay.server.util.JSONUtil;
import com.ribay.server.util.session.SessionData;
import com.ribay.server.util.session.SessionUtil;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by CD on 09.07.2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = OrderServiceTest.TestConfig.class)
@WebAppConfiguration
public class OrderServiceTest {

    /**
     * Overrides the default configuration so we can autowire a mock
     */
    @Configuration
    @Import(RibayServerApplication.class)
    public static class TestConfig {
        @Bean
        public OrderRepository orderRepository() {
            return Mockito.mock(OrderRepository.class);
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    private Cookie generateSessionCookie(String sessionId, UUID userId) {
        SessionData sessionData = new SessionData();
        sessionData.setSessionId(sessionId);
        sessionData.setUser(new User("email2", "pw2", userId, "name2"));
        sessionData.setHash(SessionUtil.generateHash(sessionData));

        Cookie sessionCookie = SessionUtil.generateSessionCookie(sessionData);
        return sessionCookie;
    }

    @Before
    public void setup() throws Exception {
        // do not save order in db
        Mockito.doNothing().when(orderRepository).storeFinishedOrder(org.mockito.Matchers.any(OrderFinished.class));

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @Test
    public void testStartCheckoutNotLoggedIn() throws Exception {
        mockMvc.perform(post("/checkout/start"))
                .andExpect(status().is(HttpStatus.PRECONDITION_FAILED.value()))
                .andExpect(model().attribute("exception", IsInstanceOf.any(NotLoggedInException.class)));

    }

    @Test
    public void testStartCheckoutEmptyCart() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().is(HttpStatus.PRECONDITION_FAILED.value()))
                .andExpect(model().attribute("exception", isA(EmptyCartException.class)));
    }

    @Test
    public void testStartCheckoutSuccess() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 7;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", not(isEmptyOrNullString()))) // id must be set
                .andExpect(jsonPath("$.sessionId", is(sessionId))) // session id must match
                .andExpect(jsonPath("$.userId", is(userId.toString()))) // user id must match
                .andExpect(jsonPath("$.address", nullValue())) // no address at start
                .andExpect(jsonPath("$.cart", notNullValue())) // cart must be set
                .andExpect(jsonPath("$.cart.articles", hasSize(1))) // cart contains only one item
                .andExpect(jsonPath("$.cart.articles[0]", both(hasEntry("id", articleId)).and(hasEntry("quantity", quantity)))) // only item has the specified values
                .andExpect(jsonPath("$.dateStarted", notNullValue())) // date is set
                .andExpect(jsonPath("$.hash", not(isEmptyOrNullString()))); // hash is set
    }

    @Test
    public void testFinishCheckoutWrongHash() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 3;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        Order order = JSONUtil.read(orderBytes, Order.class);
        order.setDateStarted(order.getDateStarted() + 10); // change date without changing hash

        String orderStringNew = JSONUtil.write(order);

        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie).content(orderStringNew).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.PRECONDITION_FAILED.value()))
                .andExpect(model().attribute("exception", isA(IncorrectHashException.class)));
    }

    @Test
    public void testFinishCheckoutOrderTooOld() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 4;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        Order order = JSONUtil.read(orderBytes, Order.class);
        order.setDateStarted(0); // set start date of order further to the past so order will be too old
        order.updateHash(); // make sure order is valid

        String orderStringNew = JSONUtil.write(order);

        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie).content(orderStringNew).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.GONE.value()))
                .andExpect(model().attribute("exception", isA(OrderTooOldException.class)));
    }

    @Test
    public void testFinishCheckoutNotLoggedInAnymore() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 2;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // no cookie on finish
        mockMvc.perform(post("/checkout/finish").content(orderBytes).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.PRECONDITION_FAILED.value()))
                .andExpect(model().attribute("exception", isA(NotLoggedInException.class)));
    }

    @Test
    public void testFinishCheckoutOtherClient() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final String sessionId2 = "test2_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 2;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        Cookie sessionCookie2 = generateSessionCookie(sessionId2, userId);

        // other cookie on finish
        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie2).content(orderBytes).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.PRECONDITION_FAILED.value()))
                .andExpect(model().attribute("exception", isA(IncorrectClientException.class)));
    }

    @Test
    public void testFinishCheckoutOtherUser() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final UUID userId2 = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 2;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        Cookie sessionCookie2 = generateSessionCookie(sessionId, userId2);

        // other cookie on finish
        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie2).content(orderBytes).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.PRECONDITION_FAILED.value()))
                .andExpect(model().attribute("exception", isA(IncorrectUserException.class)));
    }

    @Test
    public void testFinishCheckoutCartChanged1() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 2;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // add article quantity after starting checkout
        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie).content(orderBytes).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(model().attribute("exception", isA(CartChangedException.class)))
                .andExpect(model().attribute("newOrder", isA(Order.class)))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", hasSize(1)))))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", everyItem(both(hasProperty("id", is(articleId))).and(hasProperty("quantity", is(quantity * 2))))))));
    }

    @Test
    public void testFinishCheckoutCartChanged2() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final String articleId2 = "2005893";
        final int quantity = 2;
        final int quantity2 = 3;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // add another article to cart after starting checkout
        mockMvc.perform(put("/cart/add/" + articleId2 + "/" + quantity2).cookie(sessionCookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie).content(orderBytes).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(model().attribute("exception", isA(CartChangedException.class)))
                .andExpect(model().attribute("newOrder", isA(Order.class)))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", hasSize(2)))))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", hasItem(both(hasProperty("id", is(articleId))).and(hasProperty("quantity", is(quantity))))))))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", hasItem(both(hasProperty("id", is(articleId2))).and(hasProperty("quantity", is(quantity2))))))));
    }

    @Test
    public void testFinishCheckoutCartChanged3() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final String articleId2 = "2005893";
        final int quantity = 2;
        final int quantity2 = 3;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        // add second article to cart after starting checkout
        mockMvc.perform(put("/cart/add/" + articleId2 + "/" + quantity2).cookie(sessionCookie))
                .andExpect(status().isOk());

        // remove first article from cart after starting checkout
        mockMvc.perform(delete("/cart/remove/" + articleId).cookie(sessionCookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie).content(orderBytes).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(model().attribute("exception", isA(CartChangedException.class)))
                .andExpect(model().attribute("newOrder", isA(Order.class)))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", hasSize(1)))))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", everyItem(both(hasProperty("id", is(articleId2))).and(hasProperty("quantity", is(quantity2))))))));
    }

    @Test
    public void testFinishCheckoutCartChanged4() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 2;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        Order order = JSONUtil.read(orderBytes, Order.class);
        int oldPrice = order.getCart().getArticles().get(0).getPrice();
        int newPrice = oldPrice + 1; // add 1 cent to price

        // change price of article that is already in cart
        mockMvc.perform(put("/article/setPrice/" + articleId + "/" + newPrice).cookie(sessionCookie))
                .andExpect(status().isOk());

        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie).content(orderBytes).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.CONFLICT.value()))
                .andExpect(model().attribute("exception", isA(CartChangedException.class)))
                .andExpect(model().attribute("newOrder", isA(Order.class)))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", hasSize(1)))))
                .andExpect(model().attribute("newOrder", hasProperty("cart", hasProperty("articles", everyItem(both(hasProperty("id", is(articleId))).and(hasProperty("price", is(newPrice))))))));
    }

    @Test
    public void testFinishCheckoutOrderMissingAddress() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 2;

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie).content(orderBytes).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().is(HttpStatus.PRECONDITION_FAILED.value()))
                .andExpect(model().attribute("exception", isA(IncompleteOrderException.class)));
    }

    @Test
    public void testFinishCheckoutSuccess() throws Exception {
        final String sessionId = "test_" + System.currentTimeMillis();
        final UUID userId = UUID.randomUUID();
        final String articleId = "1000560";
        final int quantity = 2;
        final Address address = new Address("myName", "myAdditionalInfo", "myStreet", "myZipCode", "myPlace", "myCountry");

        Cookie sessionCookie = generateSessionCookie(sessionId, userId);

        mockMvc.perform(put("/cart/add/" + articleId + "/" + quantity).cookie(sessionCookie))
                .andExpect(status().isOk());

        byte[] orderBytes = mockMvc.perform(post("/checkout/start").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        Order order = JSONUtil.read(orderBytes, Order.class);
        order.setAddress(address);
        String newOrderString = JSONUtil.write(order);

        mockMvc.perform(post("/checkout/finish").cookie(sessionCookie).content(newOrderString).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(order.getId()))) // id must match
                .andExpect(jsonPath("$.userId", is(order.getUserId()))) // user id must match
                .andExpect(jsonPath("$.address", both(hasEntry("name", address.getName())).and(hasEntry("additionalInfo", address.getAdditionalInfo())).and(hasEntry("street", address.getStreet())).and(hasEntry("zipCode", address.getZipCode())).and(hasEntry("place", address.getPlace())).and(hasEntry("country", address.getCountry())))) // address that was set
                .andExpect(jsonPath("$.articles", hasSize(1))) // cart contains only one item
                .andExpect(jsonPath("$.articles[0]", both(hasEntry("id", articleId)).and(hasEntry("quantity", quantity)))) // only item has the specified values
                .andExpect(jsonPath("$.timestamp", notNullValue())); // date is set

        // after checkout: cart must be empty:
        byte[] cartAfterCheckoutBytes = mockMvc.perform(get("/cart/").cookie(sessionCookie))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        Cart cartAfterCheckout = JSONUtil.read(cartAfterCheckoutBytes, Cart.class);
        assertTrue(cartAfterCheckout.getArticles().isEmpty());
    }

}