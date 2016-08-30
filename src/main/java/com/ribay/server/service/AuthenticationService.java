package com.ribay.server.service;

import com.ribay.server.exception.InvalidLoginException;
import com.ribay.server.exception.InvalidRegisterException;
import com.ribay.server.material.User;
import com.ribay.server.repository.AuthenticationRepository;
import com.ribay.server.util.RequestScopeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.Future;

@RestController
public class AuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private AuthenticationRepository repository;

    @Autowired
    private RequestScopeData requestData;

    @RequestMapping(path = "/auth/login", method = RequestMethod.POST)
    public User login(@RequestBody LoginDTO loginData) throws Exception {
        String emailAddress = loginData.getEmailAddress();
        String password = loginData.getPassword();

        User user = (emailAddress == null) ? null : repository.lookupExistingUser(emailAddress);

        if (user != null) {
            // User exists in database
            if ((password != null) && password.equals(user.getPassword())) {
                requestData.setUser(user); // add user to session

                String key = requestData.getSessionId();
                return repository.login(key, user);
            } else {
                // password is wrong
                throw new InvalidLoginException();
            }
        } else {
            // user does not exist
            throw new InvalidLoginException();
        }
    }

    @RequestMapping(path = "/auth/logout", method = RequestMethod.POST)
    public void logout() throws Exception {
        String key = requestData.getSessionId();
        try {
            requestData.setUser(null); // remove user from session

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
            User existingUser = repository.lookupExistingUser(user.getEmailAddress());
            if (existingUser != null) {
                throw new InvalidRegisterException();
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to look up existing user in register operation.", e);
            throw new InvalidRegisterException();
        }

        UUID uuid = UUID.randomUUID();
        LOGGER.info("Generated UUID: " + uuid.toString());
        user.setUuid(uuid);

        try {
            repository.register(user);
            return user;
        } catch (Exception e) {
            LOGGER.warn("Failed to execute register.", e);
            throw new InvalidRegisterException();
        }
    }

    public static class LoginDTO {
        private String emailAddress;
        private String password;


        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}
