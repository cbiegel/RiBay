package com.ribay.server.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RibayProperties
{

    private final Logger logger = LoggerFactory.getLogger(RibayProperties.class);

    private final Properties applicationProperties;
    private final Properties databaseProperties;

    public RibayProperties() throws Exception
    {
        logger.info("Load properties");

        applicationProperties = new Properties();
        applicationProperties.load(RibayProperties.class.getClassLoader()
                .getResourceAsStream("application.properties"));

        databaseProperties = new Properties();
        databaseProperties.load(
                RibayProperties.class.getClassLoader().getResourceAsStream("database.properties"));
    }

    public int getSessionTimeout()
    {
        String value = applicationProperties.getProperty("session.timeout.seconds");
        return Integer.parseInt(value);
    }

    public String[] getDatabaseIps() throws Exception
    {
        String value = databaseProperties.getProperty("ips");
        return value.split(",");
    }

    public String getBucketCart()
    {
        String value = databaseProperties.getProperty("bucket.cart");
        return value;
    }

    public String getBucketSessionLastAccess()
    {
        String value = databaseProperties.getProperty("bucket.session.lastaccess");
        return value;
    }

    public String getBucketSessionLogin()
    {
        String value = databaseProperties.getProperty("bucket.session.login");
        return value;
    }

}
