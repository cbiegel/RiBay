package com.ribay.server.material;

/**
 * Created by CD on 24.05.2016.
 */
public class ArticleForLastVisited extends ArticleShort {

    private long lastVisited;

    public ArticleForLastVisited() {

    }

    public long getLastVisited() {
        return lastVisited;
    }

    public void setLastVisited(long lastVisited) {
        this.lastVisited = lastVisited;
    }

}
