package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;

import java.util.List;

public interface UserService {

    User[] getUsers();

    User getUserById(int id);

    User getUserByUsername(String username);

    User getUserByAccountId(int accountId);

    void setToken(String token);

}
