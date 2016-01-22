package com.ribay.server.util;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.ribay.server.db.MyRiakClient;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AuthInterceptor extends HandlerInterceptorAdapter
{

    public static final String HEADER_NAME = "rsessionid";

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @Autowired
    private RequestScopeData requestData;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception
    {
        // only call interceptor if not using the cross-domain call. See
        // http://stackoverflow.com/questions/12111936/angularjs-performs-an-options-http-request-for-a-cross-origin-resource
        if (!RequestMethod.OPTIONS.name().equals(request.getMethod()))
        {
            String sessionId = request.getHeader(HEADER_NAME);

            if (sessionId == null)
            {
                // if no session id is set, a unique one will be created and set as response header
                // the client has to create a cookie from that response header

                // TODO use logger
                System.out.println("No session id set. create one");

                sessionId = UUID.randomUUID().toString();

                response.addHeader(HEADER_NAME, sessionId);
            }
            else
            {
                // TODO use logger
                System.out.println("session id is set");

                // TODO check if valid?
            }

            // TODO use logger
            System.out.println("session id: " + sessionId);

            // set session id of request scope data so that the actual rest services can use that to
            // get the session id
            requestData.setSessionId(sessionId);

            saveLastAccess();
        }

        return super.preHandle(request, response, handler);
    }

    private void saveLastAccess()
    {
        String bucket = properties.getBucketSessionLastAccess();
        String key = requestData.getSessionId();
        Long value = System.currentTimeMillis();

        try
        {
            Location cartObjectLocation = new Location(new Namespace(bucket), key);
            StoreValue storeOp = new StoreValue.Builder(value).withLocation(cartObjectLocation)
                    .build();
            client.execute(storeOp);
        }
        catch (Exception e)
        {
            // TODO use logger
            e.printStackTrace();
        }
    }

}
