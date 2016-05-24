package com.ribay.server.service;

import com.ribay.server.material.Article;
import com.ribay.server.material.ArticleQuery;
import com.ribay.server.material.ArticleReview;
import com.ribay.server.material.ArticleSearchResult;
import com.ribay.server.repository.ArticleRepository;
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

    @RequestMapping(path = "/article/search", method = RequestMethod.POST)
    public ArticleSearchResult searchArticles(@RequestBody ArticleQuery query) throws Exception {
        return articleRepository.queryArticles(query);
    }

    @RequestMapping(path = "/article/info", method = RequestMethod.GET)
    public Article getArticleInfo(@RequestParam(value = "articleId") String articleId) throws Exception {
        Article result = articleRepository.getArticleInformation(articleId);

        return result;
    }

    @RequestMapping(path = "/article/getReviews", method = RequestMethod.GET)
    public List<ArticleReview> getArticleReviews(@RequestParam(value = "articleId") String articleId) throws Exception {
        List<ArticleReview> reviews;

        try {
            reviews = articleRepository.getReviewsForArticle(articleId);
        } catch (Exception e) {
            LOGGER.error("Failed to get reviews for article " + articleId);
            e.printStackTrace();
            return null;
        }

        return reviews;
    }

    @RequestMapping(path = "/article/submitReview", method = RequestMethod.POST)
    public ArticleReview submitArticleReview(@RequestBody ArticleReview review) throws Exception {

        try {
            articleRepository.submitArticleReview(review);
            return review;
        } catch (Exception e) {
            LOGGER.error("Failed to submit article review.");
            e.printStackTrace();
            return null;
        }
    }

}
