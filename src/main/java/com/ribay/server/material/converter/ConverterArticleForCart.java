package com.ribay.server.material.converter;

import com.ribay.server.material.Article;
import com.ribay.server.material.ArticleShort;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Created by CD on 23.05.2016.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ConverterArticleForCart implements Converter<Article, ArticleShort> {

    @Override
    public ArticleShort convert(Article source) {
        ArticleShort target = new ArticleShort();
        target.setId(source.getId());
        target.setName(source.getTitle());
        target.setImage(source.getImageId());
        target.setPrice(1337); // TODO get price
        return target;
    }

}
