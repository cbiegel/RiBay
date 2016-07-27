package com.ribay.server.util.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JacksonUtils;
import com.ribay.server.util.RibayProperties;
import com.ribay.server.util.security.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Created by CD on 06.06.2016.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionUtil {

    private static final Logger logger = LoggerFactory.getLogger(SessionUtil.class);

    public static final String SESSION_COOKIE_NAME = "session";

    private static final Charset COOKIE_CHARSET = StandardCharsets.US_ASCII; // use ascii because it is easy to decode with js

    @Autowired
    private RibayProperties properties;

    public SessionData readSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            // if no cookies -> create session
            return createSession();
        } else {
            return Arrays.stream(cookies) //
                    .filter(cookie -> cookie.getName().equals(SESSION_COOKIE_NAME)).findAny() //
                    .map(Cookie::getValue) //
                    .map(value -> value.getBytes(COOKIE_CHARSET)) //
                    //.map(Base64::decodeBase64) //
                    .map(SessionUtil::JSONToSession) //
                    .filter(SessionUtil::isSessionValid)
                    .orElseGet(SessionUtil::createSession);
        }
    }

    public void writeSession(HttpServletResponse response, SessionData sessionData) {
        String hashValue = generateHash(sessionData);
        sessionData.setHash(hashValue);

        Cookie sessionCookie = generateSessionCookie(sessionData);
        sessionCookie.setMaxAge(properties.getSessionTimeout());
        sessionCookie.setPath("/");
        response.addCookie(sessionCookie);
    }

    public static Cookie generateSessionCookie(SessionData sessionData) {
        byte[] sessionBytes = SessionUtil.sessionToJSON(sessionData);
        String sessionValue = new String(sessionBytes, COOKIE_CHARSET);
        Cookie sessionCookie = new Cookie(SessionUtil.SESSION_COOKIE_NAME, sessionValue);
        return sessionCookie;
    }

    private static SessionData createSession() {
        SessionData sessionData = new SessionData();
        sessionData.setSessionId(UUID.randomUUID().toString());
        sessionData.setUser(null);
        sessionData.setNew(true);
        return sessionData;
    }

    private static boolean isSessionValid(SessionData session) {
        String givenHash = session.getHash();
        String generatedHash = SessionUtil.generateHash(session);
        boolean result = generatedHash.equals(givenHash);
        if (!result) {
            logger.warn("Session manipulation from client!");
        }
        return result;
    }

    private static ObjectMapper createJSONMapper() {
        ObjectMapper mapper = JacksonUtils.newMapper();
        // https://github.com/FasterXML/jackson-databind#10-minute-tutorial-configuration
        // mapper.writerWithDefaultPrettyPrinter();
        return mapper;
    }

    private static SessionData JSONToSession(byte[] json) {
        try {
            return createJSONMapper().readValue(json, SessionData.class);
        } catch (IOException e) {
            throw new RuntimeException("Error while reading session", e);
        }
    }

    private static byte[] sessionToJSON(SessionData session) {
        try {
            return createJSONMapper().writeValueAsBytes(session);
        } catch (IOException e) {
            throw new RuntimeException("Error while writing session", e);
        }
    }

    public static String generateHash(SessionData sessionData) {
        // functions to use to generate hash
        Supplier<?>[] suppliers = {sessionData::getSessionId, sessionData::getUser};
        return HashUtil.generateHash(suppliers);
    }

}
