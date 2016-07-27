package com.ribay.server.material.converter;

import com.ribay.server.material.Order;
import com.ribay.server.material.OrderFinished;
import com.ribay.server.util.clock.RibayClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Created by CD on 29.06.2016.
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ConverterOrder implements Converter<Order, OrderFinished> {

    @Autowired
    private RibayClock clock;

    @Override
    public OrderFinished convert(Order toFinish) {
        // copy everything except the hash and set current time
        OrderFinished finished = new OrderFinished();
        finished.setId(toFinish.getId());
        finished.setUserId(toFinish.getUserId());
        finished.setAddress(toFinish.getAddress());
        finished.setArticles(toFinish.getCart().getArticles());
        finished.setTimestamp(clock.getTime());
        return finished;
    }

}
