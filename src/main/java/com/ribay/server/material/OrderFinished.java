package com.ribay.server.material;

import java.util.List;

/**
 * Created by CD on 29.06.2016.
 */
public class OrderFinished {

    private String id;
    private String userId;
    private Address address;
    private List<ArticleForCart> articles;
    private long timestamp;

    public OrderFinished() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<ArticleForCart> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleForCart> articles) {
        this.articles = articles;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
