package com.hcl.service;

import com.hcl.entity.User;
import com.hcl.exception.UserAlreadyExistsException;
import com.hcl.exception.UserNotFoundException;

public interface UserService {
 void registerUser(User user) throws UserAlreadyExistsException;
 void login(String username, String password) throws UserNotFoundException;
}

