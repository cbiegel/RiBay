package com.ribay.server.material;

/**
 * Created by Chris on 17.05.2016.
 */
public class ArticleReview {

    private String articleId;
    private String author;
    private String uuid;
    private String date;
    private String ratingValue;
    private String reviewTitle;
    private String reviewContent;

    public ArticleReview()
    {

    }

    public ArticleReview(String articleId, String author, String uuid, String date, String ratingValue, String reviewTitle, String reviewContent)
    {
        this.articleId = articleId;
        this.author = author;
        this.uuid = uuid;
        this.date = date;
        this.ratingValue = ratingValue;
        this.reviewTitle = reviewTitle;
        this.reviewContent = reviewContent;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(String ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getReviewTitle() {
        return reviewTitle;
    }

    public void setReviewTitle(String reviewTitle) {
        this.reviewTitle = reviewTitle;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }
}
