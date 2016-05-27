package com.ribay.server.service;

import com.ribay.server.material.*;
import com.ribay.server.material.continuation.ArticleReviewsContinuation;
import com.ribay.server.material.converter.Converter;
import com.ribay.server.repository.ArticleRepository;
import com.ribay.server.repository.AuthenticationRepository;
import com.ribay.server.repository.MarketingRepository;
import com.ribay.server.util.RequestScopeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by CD on 01.05.2016.
 */
@RestController
public class ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private Converter<Article, ArticleShort> articleConverter;

    @Autowired
    private RequestScopeData requestData;

    @RequestMapping(path = "/article/search", method = RequestMethod.POST)
    public ArticleSearchResult searchArticles(@RequestBody ArticleQuery query) throws Exception {
        return articleRepository.queryArticles(query);
    }

    @RequestMapping(path = "/article/info", method = RequestMethod.GET)
    public Article getArticleInfo(@RequestParam(value = "articleId") String articleId) throws Exception {
        Article article = articleRepository.getArticleInformation(articleId);
        try {
            // save last visited article
            String sessionId = requestData.getSessionId();
            ArticleShort articleShort = articleConverter.convert(article);
            marketingRepository.addVisitedArticle(sessionId, articleShort); // is async: fire and forget
        } catch (Exception e) {
            LOGGER.error("Was not able to save last visited article", e);
        }
        return article;
    }

    @RequestMapping(path = "/article/getReviews", method = RequestMethod.GET)
    public ArticleReviewsContinuation getArticleReviews(@RequestParam(value = "articleId") String articleId) throws Exception {
        ArticleReviewsContinuation result = null;

        try {
            // TODO: Save User in requestData when logging in so that we can just read it here instead of querying
            User loggedInUser = authenticationRepository.getLoggedInUser(requestData.getSessionId());
            result = articleRepository.getReviewsForArticle(articleId, loggedInUser.getUuid().toString());
        } catch (Exception e) {
            LOGGER.error("Failed to get reviews for article " + articleId);
            e.printStackTrace();
            return null;
        }

        return result;
    }

    @RequestMapping(path = "/article/submitReview", method = RequestMethod.POST)
    public ArticleReview submitArticleReview(@RequestBody ArticleReview review) throws Exception {

        try {
            // TODO: Save User in requestData when logging in so that we can just read it here instead of querying
            User loggedInUser = authenticationRepository.getLoggedInUser(requestData.getSessionId());
            articleRepository.submitArticleReview(review, loggedInUser.getUuid().toString());
            return review;
        } catch (Exception e) {
            LOGGER.error("Failed to submit article review.");
            e.printStackTrace();
            return null;
        }
    }

}
