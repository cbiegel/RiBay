package com.ribay.server.util;

import com.ribay.server.material.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.INTERFACES)
public class RequestScopeDataImpl implements RequestScopeData {

    @Autowired
    private SessionUtil sessionUtil;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private SessionData session;

    @Override
    public void init(HttpServletRequest request, HttpServletResponse response, SessionData session) {
        this.request = request;
        this.response = response;
        this.session = session;
    }

    @Override
    public String getSessionId() {
        return session.getSessionId();
    }

    @Override
    public User getUser() {
        return session.getUser();
    }

    @Override
    public void setUser(User user) {
        session.setUser(user);
        updateCookie();
    }

    private void updateCookie() {
        SessionData oldSessionData = sessionUtil.readSession(request);
        SessionData newSessionData = session;
        if (!newSessionData.equals(oldSessionData)) {
            // if session data changed or there was no session data -> update cookie
            sessionUtil.writeSession(response, newSessionData);
        }
    }

}
