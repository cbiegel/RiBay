package com.ribay.server.util;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
public class RequestScopeDataImpl implements RequestScopeData
{

    private String sessionId;

    @Override
    public String getSessionId()
    {
        return sessionId;
    }

    @Override
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

}
