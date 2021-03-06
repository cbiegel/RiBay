package com.ribay.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ribay.server.material.*;
import com.ribay.server.material.continuation.ArticleReviewsContinuation;
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
    private RequestScopeData requestData;

    @RequestMapping(path = "/article/search", method = RequestMethod.POST)
    public ArticleSearchResult searchArticles(@RequestBody ArticleQuery query) throws Exception {
        return articleRepository.queryArticles(query);
    }

    @RequestMapping(path = "/article/typeahead/{text}", method = RequestMethod.GET)
    public List<ArticleShortest> getArticleTypeahead(@PathVariable(value = "text") String text) throws Exception {
        return articleRepository.getArticleTypeahead(text);
    }

    @RequestMapping(path = "/article/info", method = RequestMethod.GET)
    public Article getArticleInfo(@RequestParam(value = "articleId") String articleId) throws Exception {
        Article article = articleRepository.getArticleInformation(articleId);
        return article;
    }

    @RequestMapping(path = "/article/info/dynamic", method = RequestMethod.GET)
    public ArticleDynamic getArticleInfoDynamic(@RequestParam(value = "articleId") String articleId) throws Exception {
        ArticleDynamic article = articleRepository.getArticleInformationDynamic(articleId);
        return article;
    }

    @RequestMapping(path = "/article/reviews", method = RequestMethod.GET)
    public ArticleReviewsContinuation getArticleReviews(@RequestParam(value = "articleId") String articleId,
                                                        @RequestParam(value = "continuation", required = false) String continuation,
                                                        @RequestParam(value = "rating_range", required = false) String ratingRange) throws Exception {
        RatingFilterRange ratingRangeObj = null;
        if (ratingRange != null) {
            ratingRangeObj = new ObjectMapper().readValue(ratingRange, RatingFilterRange.class);
        }
        return articleRepository.getReviewsForArticle(articleId, continuation, ratingRangeObj);
    }

    @RequestMapping(path = "/article/submitReview", method = RequestMethod.POST)
    public ArticleReview submitArticleReview(@RequestBody ArticleReview review) throws Exception {
        User loggedInUser = requestData.getUser();
        if (loggedInUser == null) {
            throw new Exception("Not logged in!");
        } else {
            ArticleReview previousReview = isFirstReviewForArticle(review.getArticleId());
            articleRepository.submitArticleReview(review, loggedInUser.getUuid().toString(), previousReview);
            return review;
        }
    }

    @RequestMapping(path = "/article/isFirstReview", method = RequestMethod.GET)
    public ArticleReview isFirstReviewForArticle(@RequestParam(value = "articleId") String articleId) throws Exception {
        ArticleReview result = null;

        User loggedInUser = requestData.getUser();
        if (loggedInUser == null) {
            throw new Exception("Not logged in!");
        }

        result = articleRepository.iSFirstReviewForArticle(articleId, loggedInUser.getUuid().toString());

        return result;
    }

    // TODO move to AdminService
    @RequestMapping(path = "/article/changeStock/{articleId}/{diff}", method = RequestMethod.PUT)
    public Integer changeStock(
            @PathVariable(value = "articleId") String articleId,
            @PathVariable(value = "diff") int diff,
            @RequestParam(value = "returnNewValue", required = false, defaultValue = "false") boolean returnNewValue) throws Exception {
        articleRepository.changeStock(articleId, diff);
        Integer result = returnNewValue ? articleRepository.getStock(articleId) : null;
        return result;
    }

    // TODO move to AdminService
    @RequestMapping(path = "/article/setPrice/{articleId}/{price}", method = RequestMethod.PUT)
    public void setPrice(@PathVariable(value = "articleId") String articleId, @PathVariable(value = "price") int price) throws Exception {
        articleRepository.setPrice(articleId, price);
    }

}
