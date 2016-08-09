package com.ribay.server.service;

import com.ribay.server.material.User;
import com.ribay.server.repository.UserRepository;
import com.ribay.server.util.RequestScopeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RequestScopeData requestData;
    @Autowired
    private UserRepository userRepository;

    @RequestMapping(path = "/user/editName", method = RequestMethod.POST)
    public User editUserName(@RequestBody User user) throws Exception {
        User loggedInUser = requestData.getUser();
        if(!loggedInUser.getUuid().equals(user.getUuid())) {
            return null;
        }
        userRepository.editUserName(loggedInUser.getUuid().toString(), user.getName());
        requestData.setUser(user);
        return user;
    }

    @RequestMapping(path = "/user/editPassword", method = RequestMethod.POST)
    public User editPassword(@RequestBody User user) throws Exception {
        User loggedInUser = requestData.getUser();
        if(!loggedInUser.getUuid().equals(user.getUuid())) {
            return null;
        }
        userRepository.editPassword(loggedInUser.getUuid().toString(), user.getPassword());
        requestData.setUser(user);
        return user;
    }
}
