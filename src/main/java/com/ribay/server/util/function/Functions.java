package com.ribay.server.util.function;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by CD on 15.08.2016.
 */
public class Functions {

    public static <T> Set<T> mergeSets(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>();
        result.addAll(set1);
        result.addAll(set2);
        return result;
    }

}
