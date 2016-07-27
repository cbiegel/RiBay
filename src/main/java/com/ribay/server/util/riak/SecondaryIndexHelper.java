package com.ribay.server.util.riak;

import com.basho.riak.client.api.commands.indexes.SecondaryIndexQuery;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.ribay.server.db.MyRiakClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by CD on 08.07.2016.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SecondaryIndexHelper {

    @Autowired
    private MyRiakClient client;

    public <T> List<T> fetchValues(SecondaryIndexQuery.Response<?> response, Class<T> clazz) {
        return response.getEntries().stream() //
                .parallel() // parallel for faster fetching
                .map(e -> ((SecondaryIndexQuery.Response.Entry) e))
                .map(SecondaryIndexQuery.Response.Entry::getRiakObjectLocation) // get location
                .map((location) -> {
                    try {
                        FetchValue command = new FetchValue.Builder(location).build();
                        FetchValue.Response resp = client.execute(command);
                        return resp.getValue(clazz);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }) // get value for location
                .filter(o -> (o != null)) // only when no error
                .collect(Collectors.toList());
    }

}
