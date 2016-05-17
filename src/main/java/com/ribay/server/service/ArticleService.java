package com.ribay.server.service;

import java.util.ArrayList;
import java.util.List;

import com.ribay.server.material.ArticleReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ribay.server.material.Article;
import com.ribay.server.material.ArticleQuery;
import com.ribay.server.material.PageInfo;
import com.ribay.server.repository.ArticleRepository;

/**
 * Created by CD on 01.05.2016.
 */
@RestController
public class ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    @Autowired
    private ArticleRepository articleRepository;

    @RequestMapping(path = "/article/search", method = RequestMethod.GET)
    public List<Article> searchArticles() throws Exception {
        // TODO pass arguments from browser
        ArticleQuery query = new ArticleQuery();
        query.setText("So");
        query.setMovie(null);
        query.setGenre(null);
        query.setPrice_low(null);
        query.setPrice_high(null);
        query.setRating_low(null);
        query.setRating_high(null);
        query.setVotes_low(null);
        query.setVotes_high(null);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage_no(1);
        pageInfo.setPage_size(20);

        return articleRepository.queryArticles(query, pageInfo);
    }

    @RequestMapping(path = "/article/info", method = RequestMethod.GET)
    public Article getArticleInfo(@RequestParam(value = "articleId") String articleId) throws Exception {
        return articleRepository.getArticleInformation(articleId);
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

        try
        {
            articleRepository.submitArticleReview(review);
            return review;
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to submit article review.");
            e.printStackTrace();
            return null;
        }
    }

}
