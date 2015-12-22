package com.ribay.server;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MyRiakClient extends RiakClient
{

    public MyRiakClient() throws Exception
    {
        super(setUpCluster());
    }

    private static RiakCluster setUpCluster() throws Exception
    {
        InputStream is = MyRiakClient.class.getClassLoader()
                .getResourceAsStream("database.properties");

        if (is == null)
        {
            throw new Exception("No file 'database.properties' in classpath");
        }

        Properties p = new Properties();
        p.load(is);

        String ipsProperty = p.getProperty("ips");

        if (ipsProperty == null)
        {
            throw new Exception("No property 'ips' configured in database.properties");
        }

        String[] ips = ipsProperty.split(",");

        List<RiakNode> nodes = new ArrayList<>();
        for (String ip : ips)
        {
            RiakNode node = new RiakNode.Builder().withRemoteAddress(ip).withRemotePort(8087)
                    .build();
            nodes.add(node);
        }

        RiakCluster cluster = new RiakCluster.Builder(nodes).build();

        // The cluster must be started to work, otherwise you will see errors
        cluster.start();

        return cluster;
    }

}
