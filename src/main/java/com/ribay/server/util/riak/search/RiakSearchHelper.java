package com.ribay.server.util.riak.search;

import java.util.List;
import java.util.Map;

/**
 * Created by CD on 06.06.2016.
 */
public interface RiakSearchHelper {

    public default String getString(String field, Map<String, List<String>> map) {
        return getString(field, map, null); // default value: null
    }

    public String getString(String field, Map<String, List<String>> map, String defaultValue);

    public default int getInteger(String field, Map<String, List<String>> map) {
        return getInteger(field, map, 0); // default value: 0
    }

    public int getInteger(String field, Map<String, List<String>> map, int defaultValue);

    public default long getLong(String field, Map<String, List<String>> map) {
        return getLong(field, map, 0L); // default value: 0
    }

    public long getLong(String field, Map<String, List<String>> map, long defaultValue);

    public default boolean getBoolean(String field, Map<String, List<String>> map) {
        return getBoolean(field, map, false); // default value: false
    }

    public boolean getBoolean(String field, Map<String, List<String>> map, boolean defaultValue);

}
