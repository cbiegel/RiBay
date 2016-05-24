package com.ribay.server.service;

import com.ribay.server.material.Article;
import com.ribay.server.material.ArticleShort;
import com.ribay.server.material.Cart;
import com.ribay.server.material.converter.Converter;
import com.ribay.server.repository.ArticleRepository;
import com.ribay.server.repository.CartRepository;
import com.ribay.server.util.RequestScopeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private Converter<Article, ArticleShort> articleConverter;

    @Autowired
    private RequestScopeData requestData;

    @RequestMapping(path = "/cart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Cart getCart() throws Exception {
        String sessionId = requestData.getSessionId();

        return cartRepository.getCart(sessionId);
    }

    @RequestMapping(path = "/cart/add/{articleId}/{amount}", method = RequestMethod.PUT)
    public void addArticle(@PathVariable("articleId") String articleId, @PathVariable("amount") int amount) throws Exception {
        String sessionId = requestData.getSessionId();

        Article article = articleRepository.getArticleInformation(articleId);
        ArticleShort articleShort = articleConverter.convert(article);

        cartRepository.changeArticleAmount(sessionId, articleShort, amount);
    }

    @RequestMapping(path = "/cart/remove/{articleId}/{amount}", method = RequestMethod.PUT)
    public void removeArticle(@PathVariable("articleId") String articleId, @PathVariable("amount") int amount) throws Exception {
        String sessionId = requestData.getSessionId();

        Article article = articleRepository.getArticleInformation(articleId);
        ArticleShort articleShort = articleConverter.convert(article);

        cartRepository.changeArticleAmount(sessionId, articleShort, -amount);
    }

    @RequestMapping(path = "/cart/remove/{articleId}", method = RequestMethod.DELETE)
    public Cart removeArticle(@PathVariable("articleId") String articleId) throws Exception {
        String sessionId = requestData.getSessionId();

        Article article = articleRepository.getArticleInformation(articleId);
        ArticleShort articleShort = articleConverter.convert(article);

        return cartRepository.removeArticle(sessionId, articleShort);
    }

}
