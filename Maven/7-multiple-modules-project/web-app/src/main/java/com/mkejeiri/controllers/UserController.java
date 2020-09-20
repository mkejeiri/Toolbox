package com.mkejeiri.controllers;

import com.mkejeiri.converters.UserMapper;
import com.mkejeiri.domain.UserCommand;
import com.mkejeiri.entities.User;

public class UserController {

    User saveUser(UserCommand command) {

        // fake impl
        return UserMapper.INSTANCE.userCommandToUser(command);
    }

}
