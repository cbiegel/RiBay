package com.ribay.server.service;

import java.util.UUID;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ribay.server.material.User;
import com.ribay.server.repository.AuthenticationRepository;
import com.ribay.server.util.RequestScopeData;

@RestController
public class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private AuthenticationRepository repository;

    @Autowired
    private RequestScopeData requestData;

    @RequestMapping(path = "/auth/loggedin", method = RequestMethod.GET)
    public User getLoggedInUser() throws Exception {

        String key = requestData.getSessionId();
        return repository.getLoggedInUser(key);
    }

    @RequestMapping(path = "/auth/login", method = RequestMethod.POST)
    public User login(@RequestParam(value = "email") String emailAddress,
                      @RequestParam(value = "password") String password) throws Exception {
        // TODO encrypt password and send as header
        User user = null;
        user = repository.lookupExistingUser(emailAddress);

        // User exists in database
        if (null != user) {
            String key = requestData.getSessionId();
            return repository.login(key, user);
        } else {
            return null;
        }
    }

    @RequestMapping(path = "/auth/logout", method = RequestMethod.POST)
    public void logout() throws Exception {
        String key = requestData.getSessionId();
        try {
            Future<?> future = repository.logout(key);
            future.get(); // wait for
        } catch (Exception e) {
            LOGGER.warn("Failed to execute logout.", e);
        }
    }

    @RequestMapping(path = "/auth/register", method = RequestMethod.POST)
    public User register(@RequestBody User user) throws Exception {
        // Look up if the user with the given email address already exists
        // TODO: This operation is not transactional. It might be possible that a new user is
        // created in the meantime (dirty read)
        try {
            User existingUser = null;
            existingUser = repository.lookupExistingUser(user.getEmailAddress());
            if (null != existingUser) {
                return null;
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to look up existing user in register operation.", e);
            return null;
        }

        UUID uuid = UUID.randomUUID();
        LOGGER.info("Generated UUID: " + uuid.toString());
        user.setUuid(uuid);

        try {
            repository.register(user);
            return user;
        } catch (Exception e) {
            LOGGER.warn("Failed to execute register.", e);
            return null;
        }
    }
}
