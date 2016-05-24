package com.ribay.server.service;

import com.ribay.server.material.ArticleForLastVisited;
import com.ribay.server.repository.MarketingRepository;
import com.ribay.server.util.RequestScopeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by CD on 24.05.2016.
 */
@RestController
public class MarketingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketingService.class);

    @Autowired
    private MarketingRepository marketingRepository;

    @Autowired
    private RequestScopeData requestData;

    @RequestMapping(path = "/article/lastvisited", method = RequestMethod.GET)
    public List<ArticleForLastVisited> getLastVisitedArticles() throws Exception {
        String sessionId = requestData.getSessionId();
        List<ArticleForLastVisited> result = marketingRepository.getLastVisitedArticles(sessionId, 20); // TODO pass limit as parameter
        return result;
    }

}
