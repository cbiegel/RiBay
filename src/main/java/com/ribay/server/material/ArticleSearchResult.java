package com.ribay.server.material;

import java.util.List;

/**
 * Created by CD on 18.05.2016.
 */
public class ArticleSearchResult {

    private final List<ArticleForSearch> articles;
    private final int numResults;

    public ArticleSearchResult(List<ArticleForSearch> articles, int numResults) {
        this.articles = articles;
        this.numResults = numResults;
    }

    public List<ArticleForSearch> getArticles() {
        return articles;
    }

    public int getNumResults() {
        return numResults;
    }

}
