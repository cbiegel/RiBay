package com.ribay.server.util.riak;

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
    public String getString(String field, Map<String, List<String>> map) {
        return map.getOrDefault(field, Collections.singletonList(null)).get(0);
    }

    @Override
    public int getInteger(String field, Map<String, List<String>> map) {
        return Integer.valueOf(map.getOrDefault(field, Collections.singletonList("0")).get(0));
    }

    @Override
    public boolean getBoolean(String field, Map<String, List<String>> map) {
        return Boolean.valueOf(map.getOrDefault(field, Collections.singletonList("false")).get(0));
    }

}
