package com.ribay.server.service;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.basho.riak.client.api.commands.buckets.FetchBucketProperties;
import com.basho.riak.client.core.operations.FetchBucketPropsOperation;
import com.basho.riak.client.core.query.BucketProperties;
import com.sun.org.apache.xml.internal.utils.NameSpace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.basho.riak.client.api.commands.buckets.ListBuckets;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.ListKeys;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.util.RibayProperties;

@RestController
public class StatusService {

    @Autowired
    private RibayProperties properties;

    @Autowired
    private MyRiakClient client;

    @RequestMapping(path = "/status/db/buckets", method = RequestMethod.GET)
    public List<String> getBuckets() throws Exception {
        ListBuckets lb = new ListBuckets.Builder("default").build();
        ListBuckets.Response lbResp = client.execute(lb);

        List<String> buckets = StreamSupport.stream(lbResp.spliterator(), false)
                .map(Namespace::getBucketNameAsString).sorted().collect(Collectors.toList());

        return buckets;
    }

    @RequestMapping(path = "/status/db/keys", method = RequestMethod.GET)
    public List<String> getKeys(@RequestParam(value = "bucket") String bucket) throws Exception {
        ListKeys lk = new ListKeys.Builder(new Namespace(bucket)).build();
        ListKeys.Response lkResp = client.execute(lk);

        List<String> keys = StreamSupport.stream(lkResp.spliterator(), false)
                .map(Location::getKeyAsString).sorted().collect(Collectors.toList());
        return keys;
    }

    @RequestMapping(path = "/status/db/value", method = RequestMethod.GET)
    public Object getValue(@RequestParam(value = "bucket") String bucket,
                           @RequestParam(value = "key") String key) throws Exception {
        Namespace quotesBucket = new Namespace(bucket);
        Location quoteObjectLocation = new Location(quotesBucket, key);

        FetchValue fetchOp = new FetchValue.Builder(quoteObjectLocation).build();
        return client.execute(fetchOp).getValue(Object.class);
    }

    @RequestMapping(path = "/status/db/bucket_properties", method = RequestMethod.GET)
    public BucketProperties getBucketProperties(@RequestParam(value = "bucket") String bucket) throws Exception {
        Namespace namespace = new Namespace(bucket);
        FetchBucketProperties command = new FetchBucketProperties.Builder(namespace).build();
        FetchBucketPropsOperation.Response response = client.execute(command);
        return response.getBucketProperties();
    }

    @RequestMapping(path = "/status/db/cluster", method = RequestMethod.GET)
    public Map<String, Object> getClusterStatus() throws Exception {
        // no native api for that. use http instead

        Map<String, Object> result = Arrays.stream(properties.getDatabaseIps())
                .map((ip) -> "http://" + ip + ":8098/stats") // build stats url
                .parallel().map(
                        (url) -> new RestTemplate().getForObject(url, Object.class))
                .collect(Collectors.toMap(
                        ((jsonObj) -> (String) ((Map<?, ?>) jsonObj).get("nodename")), // key
                        ((jsonObj) -> jsonObj), // use the object itself as value
                        ((jsonObj1, jsonObj2) -> jsonObj2), // if conflict -> use last object
                        (() -> new TreeMap<>(StatusService::compareNodeNames)))); // store sorted

        return result;
    }

    private static int compareNodeNames(String nodeName1, String nodeName2) {
        try {
            // remove 'riak@' prefix
            String ip1 = nodeName1.split("@")[1];
            String ip2 = nodeName2.split("@")[1];

            // convert to byte representation
            InetAddress ia1 = InetAddress.getByName(ip1);
            InetAddress ia2 = InetAddress.getByName(ip2);

            // convert to number
            BigInteger value1 = new BigInteger(ia1.getAddress());
            BigInteger value2 = new BigInteger(ia2.getAddress());

            // compare number
            return value1.subtract(value2).signum();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
