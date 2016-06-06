package com.ribay.server.util.riak.search;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by CD on 06.06.2016.
 */
public interface RiakSearchHelper {

    public String getString(String field, Map<String, List<String>> map);

    public int getInteger(String field, Map<String, List<String>> map);

    public boolean getBoolean(String field, Map<String, List<String>> map);

}
