package com.ribay.server.repository;

import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.exception.NotFoundException;
import com.ribay.server.util.RibayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

/**
 * Created by CD on 02.05.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ImageRepository {

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    public byte[] loadImage(String imageId) throws NotFoundException, Exception {
        String bucket = properties.getBucketArticles();
        String key = imageId;

        Location location = new Location(new Namespace(bucket), key);
        FetchValue command = new FetchValue.Builder(location).build();
        FetchValue.Response response = client.execute(command);
        if (response.isNotFound()) {
            throw new NotFoundException();
        } else {
            byte[] result = response.getValue(new TypeReference<byte[]>() {
            });
            return result;
        }
    }

    public Future<?> storeImage(byte[] data, String imageId) throws Exception {
        String bucket = properties.getBucketArticles();
        String key = imageId;

        Location location = new Location(new Namespace(bucket), key);
        StoreValue command = new StoreValue.Builder(data).withLocation(location).build();
        RiakFuture<StoreValue.Response, Location> response = client.executeAsync(command);
        return response;
    }

}
