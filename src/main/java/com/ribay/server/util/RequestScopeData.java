package com.ribay.server.util;

import com.ribay.server.material.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface RequestScopeData
{

    public void init(HttpServletRequest request, HttpServletResponse response, SessionData session);

    public String getSessionId();

    public User getUser();

    public void setUser(User user);

}
