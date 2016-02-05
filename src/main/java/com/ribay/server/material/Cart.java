package com.ribay.server.material;

import java.util.ArrayList;
import java.util.List;

public class Cart
{

    private List<ArticleShort> articles;

    public Cart()
    {
        this(new ArrayList<>());
    }

    public Cart(List<ArticleShort> articles)
    {
        this.articles = articles;
    }

    public List<ArticleShort> getArticles()
    {
        return articles;
    }

    public void setArticles(List<ArticleShort> articles)
    {
        this.articles = articles;
    }

}
