package com.ribay.server.util;

import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    public static final String HEADER_NAME = "rsessionid";

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @Autowired
    private RequestScopeData requestData;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        String sessionId = request.getHeader(HEADER_NAME);

        if (sessionId == null) {
            // if no session id is set, a unique one will be created and set as response header
            // the client has to create a cookie from that response header

            sessionId = UUID.randomUUID().toString();

            logger.info("No session id set. create one: " + sessionId);

            response.addHeader(HEADER_NAME, sessionId);
        } else {
            logger.debug("session id is already set: " + sessionId);

            // TODO check if valid?
        }

        // set session id of request scope data so that the actual rest services can use that to
        // get the session id
        requestData.setSessionId(sessionId);

        saveLastAccess();

        return super.preHandle(request, response, handler);
    }

    private void saveLastAccess() {
        String bucket = properties.getBucketSessionLastAccess();
        String key = requestData.getSessionId();
        Long value = System.currentTimeMillis();

        Location cartObjectLocation = new Location(new Namespace(bucket), key);
        StoreValue storeOp = new StoreValue.Builder(value).withLocation(cartObjectLocation).build();
        client.executeAsync(storeOp); // exceute async
    }

}
