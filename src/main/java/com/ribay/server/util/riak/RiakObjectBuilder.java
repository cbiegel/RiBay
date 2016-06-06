package com.ribay.server.util.riak;

import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.query.indexes.RiakIndex;
import com.basho.riak.client.core.util.BinaryValue;
import com.ribay.server.util.JSONUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CD on 27.05.2016.
 */
public class RiakObjectBuilder {

    private final Object object;
    private final Map<RiakIndex.Name, Object> indexes;

    public RiakObjectBuilder(Object object) {
        this.object = object;
        this.indexes = new HashMap<>();
    }

    /**
     * Index type must match value type!
     *
     * @param index
     * @param value
     * @return
     */
    public RiakObjectBuilder withIndex(RiakIndex.Name index, Object value) {
        indexes.put(index, value);
        return this;
    }

    public RiakObject build() {
        RiakObject result = new RiakObject();
        result.setContentType("application/json");
        result.setValue(BinaryValue.create(JSONUtil.write(object)));
        for (Map.Entry<RiakIndex.Name, Object> indexAndValue : indexes.entrySet()) {
            RiakIndex.Name index = indexAndValue.getKey();
            Object value = indexAndValue.getValue();
            result.getIndexes().getIndex(index).add(value);
        }
        return result;
    }

}
