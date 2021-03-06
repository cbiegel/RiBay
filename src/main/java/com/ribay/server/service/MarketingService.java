package com.ribay.server.service;

import com.ribay.server.exception.NotLoggedInException;
import com.ribay.server.job.AprioriJob;
import com.ribay.server.material.ArticleForLastVisited;
import com.ribay.server.material.ArticleShort;
import com.ribay.server.material.ArticleShortest;
import com.ribay.server.material.User;
import com.ribay.server.material.converter.Converter;
import com.ribay.server.repository.ArticleRepository;
import com.ribay.server.repository.MarketingRepository;
import com.ribay.server.util.RequestScopeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by CD on 24.05.2016.
 */
@RestController
public class MarketingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketingService.class);

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private AprioriJob aprioriJob;

    @Autowired
    private RequestScopeData requestData;

    @Autowired
    private Converter<ArticleShort, ArticleShortest> articleConverter;

    @RequestMapping(path = "/article/visit/{articleId}", method = RequestMethod.GET)
    public void visitArticle(@PathVariable(value = "articleId") String articleId) throws Exception {
        // save last visited article
        String sessionId = requestData.getSessionId();
        ArticleShort articleShort = articleRepository.getArticleShort(articleId);
        ArticleShortest articleShortest = articleConverter.convert(articleShort);
        marketingRepository.addVisitedArticle(sessionId, articleShortest); // is async: fire and forget
    }

    @RequestMapping(path = "/article/lastVisited", method = RequestMethod.GET)
    public List<ArticleForLastVisited> getLastVisitedArticles() throws Exception {
        String sessionId = requestData.getSessionId();
        List<ArticleForLastVisited> result = marketingRepository.getLastVisitedArticles(sessionId, 20); // TODO pass limit as parameter
        return result;
    }

    @RequestMapping(path = "/article/recommended/{articleId}", method = RequestMethod.GET)
    public List<ArticleShortest> getRecommendedArticles(@PathVariable(value = "articleId") String articleId) throws Exception {
        List<ArticleShortest> result = marketingRepository.getRecommendedArticlesForArticle(articleId);
        return result;
    }

    @RequestMapping(path = "/article/recommended", method = RequestMethod.GET)
    public List<ArticleShortest> getRecommendedArticlesForUser() throws Exception {
        User user = requestData.getUser();
        if (user == null) {
            throw new NotLoggedInException();
        } else {
            List<ArticleShortest> result = marketingRepository.getRecommendedArticlesForUser(user.getUuid().toString());
            return result;
        }
    }

    @RequestMapping(path = "/apriori", method = RequestMethod.GET)
    public void apriori() throws Exception {
        aprioriJob.start();
    }

}
