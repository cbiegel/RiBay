package com.ribay.server.material.converter;

/**
 * Created by CD on 23.05.2016.
 */
public interface Converter<S, T> {

    /**
     * Converts the specified source to a target
     *
     * @param source
     * @return
     */
    public T convert(S source);

}
