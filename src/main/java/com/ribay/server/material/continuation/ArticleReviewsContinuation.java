package com.ribay.server.material.continuation;

import com.ribay.server.material.ArticleReview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 27.05.2016.
 */
public class ArticleReviewsContinuation {

    private List<ArticleReview> reviews;
    private String continuation;

    public ArticleReviewsContinuation() {
        reviews = new ArrayList<>();
    }

    public List<ArticleReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<ArticleReview> reviews) {
        this.reviews = reviews;
    }

    public String getContinuation() {
        return continuation;
    }

    public void setContinuation(String continuation) {
        this.continuation = continuation;
    }
}