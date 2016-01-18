package com.ribay.server.service;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ribay.server.RibayProperties;

@RestController
public class SessionService
{

    private static final String COOKIE_NAME = "RSESSIONID";

    @Autowired
    private RibayProperties properties;

    @CrossOrigin(origins = "*", allowedHeaders = "*", allowCredentials = "true", methods =
    { RequestMethod.POST })
    @RequestMapping(name = "/keepSessionAlive", method = RequestMethod.POST)
    public void keepSessionAlive(
            @CookieValue(name = COOKIE_NAME, required = false) String sessionId,
            HttpServletRequest request, HttpServletResponse response)
    {
        // if no session id set -> set new session id
        if (sessionId == null)
        {
            sessionId = UUID.randomUUID().toString();
        }

        // reset cookie by creating new cookie
        Cookie cookie = new Cookie(COOKIE_NAME, sessionId);
        // cookie expires after configured interval (default 1 day)
        cookie.setMaxAge(properties.getSessionTimeout());
        cookie.setPath("/");
        cookie.setDomain("127.0.0.1");
        response.addCookie(cookie);

        // TODO save session in DB (last access, all active sessions?)
    }

    // TODO add housekeeper for cleaning data of expired sessions

}
