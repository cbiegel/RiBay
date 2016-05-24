package com.ribay.server.util.clock;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Created by CD on 24.05.2016.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
public class RibayClockImpl implements RibayClock {

    @Override
    public long getTime() {
        // use system time. maybe there is a better way?
        return System.currentTimeMillis();
    }

}
