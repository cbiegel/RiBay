package com.ribay.server.repository;

import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.exception.NotFoundException;
import com.ribay.server.util.RibayProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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

    public ImageData loadImage(String imageId) throws NotFoundException, Exception {
        String bucket = properties.getBucketImages();
        String key = imageId;

        Location location = new Location(new Namespace(bucket), key);
        FetchValue command = new FetchValue.Builder(location).build();
        FetchValue.Response response = client.execute(command);
        if (response.isNotFound()) {
            throw new NotFoundException();
        } else {
            RiakObject obj = response.getValue(RiakObject.class);

            byte[] data = obj.getValue().getValue();
            String mimeType = obj.getContentType();

            return new ImageData(data, mimeType);
        }
    }

    public static class ImageData {
        public final byte[] data;
        public final String mimeType;

        public ImageData(byte[] data, String mimeType) {
            this.data = data;
            this.mimeType = mimeType;
        }
    }

}
