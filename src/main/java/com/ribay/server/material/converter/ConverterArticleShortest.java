package com.ribay.server.material.converter;

import com.ribay.server.material.ArticleShort;
import com.ribay.server.material.ArticleShortest;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Created by CD on 06.07.2016.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ConverterArticleShortest implements Converter<ArticleShort, ArticleShortest> {

    @Override
    public ArticleShortest convert(ArticleShort source) {
        ArticleShortest result = new ArticleShortest();
        result.setId(source.getId());
        result.setName(source.getName());
        result.setImage(source.getImage());
        return result;
    }

}
