package com.ribay.server.util.riak.search;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by CD on 06.06.2016.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
public class RiakSearchHelperImpl implements RiakSearchHelper {

    @Override
    public String getString(String field, Map<String, List<String>> map, String defaultValue) {
        return map.getOrDefault(field, Collections.singletonList(defaultValue)).get(0);
    }

    @Override
    public int getInteger(String field, Map<String, List<String>> map, int defaultValue) {
        return Integer.valueOf(map.getOrDefault(field, Collections.singletonList(String.valueOf(defaultValue))).get(0));
    }

    @Override
    public long getLong(String field, Map<String, List<String>> map, long defaultValue) {
        return Long.valueOf(map.getOrDefault(field, Collections.singletonList(String.valueOf(defaultValue))).get(0));
    }

    @Override
    public boolean getBoolean(String field, Map<String, List<String>> map, boolean defaultValue) {
        return Boolean.valueOf(map.getOrDefault(field, Collections.singletonList(String.valueOf(defaultValue))).get(0));
    }

}
