package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao){
        this.userDao = userDao;
    }

    @GetMapping
    public List<UserDto> getUsers(){
        List<UserDto> userDtos = new ArrayList<>();
        List<User> users = userDao.getUsers();
            for (User u : users) {
                userDtos.add(mapUserDto(u));
            }

        return userDtos;
    }


    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id, Principal principal){
        if (id != userDao.getUserByUsername(principal.getName()).getId()) {
             throw new ResponseStatusException (HttpStatus.FORBIDDEN, "The logged in user isn't allowed to perform " + 
                    "this action because they don't have permission.",null);
        }
        return userDao.getUserById(id);
    }


    @GetMapping("/username={username}")
    public User getUserByUsername(@PathVariable String username, Principal principal){
        if (userDao.getUserByUsername(username).getId() != userDao.getUserByUsername(principal.getName()).getId()) {
            throw new ResponseStatusException (HttpStatus.FORBIDDEN, "The logged in user isn't allowed to perform " + 
                   "this action because they don't have permission.",null);
       }
        return userDao.getUserByUsername(username);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/accountId/{accountId}")
    public User getUserByAccountId(@PathVariable int accountId, Principal principal){
        if (userDao.getUserByAccountId(accountId).getId() != userDao.getUserByUsername(principal.getName()).getId()) {
            throw new ResponseStatusException (HttpStatus.FORBIDDEN, "The logged in user isn't allowed to perform " + 
                   "this action because they don't have permission.",null);
        }
        return userDao.getUserByAccountId(accountId);
    }

    public UserDto mapUserDto(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        return userDto;
    }

}
