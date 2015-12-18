package com.ribay.server;

import java.net.UnknownHostException;
import java.util.Arrays;

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

    // This will create a client object that we can use to interact with Riak
    private static RiakCluster setUpCluster() throws UnknownHostException
    {
        // This example will use only one node listening on localhost:10017
        RiakNode node1 = new RiakNode.Builder().withRemoteAddress("134.100.11.158")
                .withRemotePort(8087).build();
        RiakNode node2 = new RiakNode.Builder().withRemoteAddress("134.100.11.159")
                .withRemotePort(8087).build();
        RiakNode node3 = new RiakNode.Builder().withRemoteAddress("134.100.11.160")
                .withRemotePort(8087).build();

        // This cluster object takes our one node as an argument
        RiakCluster cluster = new RiakCluster.Builder(Arrays.asList(node1, node2, node3)).build();

        // The cluster must be started to work, otherwise you will see errors
        cluster.start();

        return cluster;
    }

}
