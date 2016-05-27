package com.ribay.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by CD on 23.05.2016.
 */
public class JSONUtil {

    public static <T> T read(byte[] bytes, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(bytes, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error while reading object from bytes", e);
        }
    }

    public static <T> String write(T object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Error while writing object to string", e);
        }
    }

}
