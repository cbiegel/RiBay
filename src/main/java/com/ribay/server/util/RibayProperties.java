package com.ribay.server.util;

import com.basho.riak.client.core.query.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RibayProperties {

    private final Logger logger = LoggerFactory.getLogger(RibayProperties.class);

    private final Properties applicationProperties;
    private final Properties databaseProperties;

    public RibayProperties() throws Exception {
        logger.info("Load properties");

        applicationProperties = new Properties();
        applicationProperties.load(RibayProperties.class.getClassLoader()
                .getResourceAsStream("application.properties"));

        databaseProperties = new Properties();
        databaseProperties.load(
                RibayProperties.class.getClassLoader().getResourceAsStream("database.properties"));
    }

    public long getSessionTimeout() {
        String value = applicationProperties.getProperty("session.timeout.seconds");
        return Long.parseLong(value);
    }

    public String[] getDatabaseIps() throws Exception {
        String value = databaseProperties.getProperty("ips");
        return value.split(",");
    }

    public String getBucketSessionLastAccess() {
        String value = databaseProperties.getProperty("bucket.session.lastaccess");
        return value;
    }

    public String getBucketSessionLogin() {
        String value = databaseProperties.getProperty("bucket.session.login");
        return value;
    }

    public Namespace getBucketSessionCart() {
        String[] attrs = databaseProperties.getProperty("bucket.session.cart").split("#");
        return buildBucketFromAttrs(attrs);
    }

    public Namespace getBucketVisitedArticles() {
        String[] attrs = databaseProperties.getProperty("bucket.session.visitedArticles").split("#");
        return buildBucketFromAttrs(attrs);
    }

    public String getBucketUsers() {
        String value = databaseProperties.getProperty("bucket.users");
        return value;
    }

    public String getBucketArticles() {
        String value = databaseProperties.getProperty("bucket.articles");
        return value;
    }

    public String getBucketImages() {
        String value = databaseProperties.getProperty("bucket.images");
        return value;
    }

    public String getBucketArticleReviews() {
        String value = databaseProperties.getProperty("bucket.articleReviews");
        return value;
    }

    private Namespace buildBucketFromAttrs(String... attrs) {
        String bucketType;
        String bucketName;
        if (attrs.length == 1) {
            bucketType = Namespace.DEFAULT_BUCKET_TYPE; // 'default'
            bucketName = attrs[0];
        } else if (attrs.length == 2) {
            bucketType = attrs[0];
            bucketName = attrs[1];
        } else {
            throw new IllegalArgumentException();
        }
        return new Namespace(bucketType, bucketName);
    }

}
