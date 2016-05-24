package com.ribay.server.util.clock;

/**
 * Created by CD on 24.05.2016.
 */
public interface RibayClock {

    /**
     * Returns a timestamp representing the current time. Please use this in the whole project when trying to get the current time so that all these processes will have the same clock.
     *
     * @return the difference, measured in milliseconds, between
     * the current time and midnight, January 1, 1970 UTC.
     */
    public long getTime();

}
