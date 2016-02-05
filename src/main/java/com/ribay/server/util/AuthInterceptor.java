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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthInterceptor extends HandlerInterceptorAdapter {

    private final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    public static final String COOKIE_NAME = "rsessionid";

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @Autowired
    private RequestScopeData requestData;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        Optional<Cookie> sessionCookie = getSessionCookie(request);

        String sessionId;
        if (!sessionCookie.isPresent()) {
            // if no session id is set, a unique one will be created and set as response header
            // the client has to create a cookie from that response header

            sessionId = UUID.randomUUID().toString();

            logger.info("No session id set. create one: " + sessionId);

            Cookie newCookie = new Cookie(COOKIE_NAME, sessionId);
            newCookie.setMaxAge(60 * 60 * 24); // one day
            newCookie.setPath("/");
            response.addCookie(newCookie);

        } else {
            Cookie existingCookie = sessionCookie.get();
            sessionId = existingCookie.getValue();

            logger.debug("session id is already set: " + sessionId);

            // TODO check if valid?
        }

        // set session id of request scope data so that the actual rest services can use that to
        // get the session id
        requestData.setSessionId(sessionId);

        saveLastAccess();

        return super.preHandle(request, response, handler);
    }

    private Optional<Cookie> getSessionCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        } else {
            return Arrays.stream(request.getCookies()).filter((cookie) -> cookie.getName().equals(COOKIE_NAME)).findAny();
        }
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
