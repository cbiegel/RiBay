package com.ribay.server.material;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {

    private List<ArticleForCartWithQ> articles;

    public Cart() {
        this(Collections.emptyList());
    }

    public Cart(List<ArticleForCartWithQ> articles) {
        this.articles = new ArrayList<>(articles);
    }

    public List<ArticleForCartWithQ> getArticles() {
        return Collections.unmodifiableList(articles);
    }

    public void removeArticle(ArticleForCartWithQ article)
    {

    }

}
