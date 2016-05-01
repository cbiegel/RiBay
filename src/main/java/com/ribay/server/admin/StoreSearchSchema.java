package com.ribay.server.admin;

import com.basho.riak.client.api.commands.buckets.StoreBucketProperties;
import com.basho.riak.client.api.commands.search.StoreSchema;
import com.basho.riak.client.core.RiakFuture;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.search.YokozunaSchema;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.util.RibayProperties;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by CD on 30.04.2016.
 */
public class StoreSearchSchema {

    public static void main(String[] args) throws Exception {
        StoreSearchSchema o = new StoreSearchSchema();
        try {
            // uncomment to perform action
            // o.createSchema();
            // o.applySchema();
        } finally {
            o.shutdown();
        }
    }

    private final RibayProperties properties;
    private final MyRiakClient client;

    public StoreSearchSchema() throws Exception {
        properties = new RibayProperties();

        client = new MyRiakClient();
        ReflectionUtils.findField(client.getClass(), "properties").setAccessible(true);
        ReflectionUtils.findField(client.getClass(), "properties").set(client, properties);
        ReflectionUtils.findMethod(client.getClass(), "init").setAccessible(true);
        ReflectionUtils.findMethod(client.getClass(), "init").invoke(client);
    }

    private void createSchema() throws Exception {
        InputStream is = StoreSearchSchema.class.getClassLoader().getResourceAsStream("schema/schema_article.xml");
        String xml = StreamUtils.copyToString(is, StandardCharsets.UTF_8);

        YokozunaSchema schema = new YokozunaSchema("schema_article", xml);
        StoreSchema command = new StoreSchema.Builder(schema).build();
        client.execute(command);
    }

    private void applySchema() throws Exception {
        String bucket = properties.getBucketArticles();

        Namespace namespace = new Namespace(bucket);
        StoreBucketProperties command = new StoreBucketProperties.Builder(namespace).withSearchIndex("schema_article").build();
        client.execute(command);
    }

    private void shutdown() throws Exception {
        client.getRiakClient().shutdown();
    }

}
