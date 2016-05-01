package com.ribay.server.service;

import com.ribay.server.material.Article;
import com.ribay.server.material.ArticleQuery;
import com.ribay.server.material.PageInfo;
import com.ribay.server.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by CD on 01.05.2016.
 */
@RestController
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @RequestMapping(path = "/article/search", method = RequestMethod.GET)
    public List<Article> searchArticles() throws Exception {
        // TODO pass arguments from browser
        ArticleQuery query = new ArticleQuery();
        query.setText("This");
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

}
