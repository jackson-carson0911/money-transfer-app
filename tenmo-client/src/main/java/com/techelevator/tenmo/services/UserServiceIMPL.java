package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceIMPL implements UserService{

    public static final String API_BASE_URL = "http://localhost:8080/user/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String token = null;

    public void setToken(String token) {
        this.token = token;
    }

    public UserServiceIMPL(){
    }
//get all users.
    public User[] getUsers() {
        User[] user = null;
        try{
            HttpEntity<User[]> entity = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeAuthEntity(), User[].class);
            user = entity.getBody();
        }catch(RestClientResponseException e){
            System.err.println("Error when retrieving users.");
            BasicLogger.log(e.getMessage());
        }
        return user;
    }
//get users by id.
    public User getUserById(int id) {
        User user = null;
        try{
            HttpEntity<User> entity = restTemplate.exchange(API_BASE_URL + id, HttpMethod.GET, makeAuthEntity(), User.class);
            user = entity.getBody();
        }catch(RestClientResponseException e){
            System.err.println("Error when retrieving user by id.");
            BasicLogger.log(e.getMessage());
        }
        return user;
    }
//get users by username.
    public User getUserByUsername(String username) {
        User user = null;
        try{
            HttpEntity<User> entity = restTemplate.exchange(API_BASE_URL + username, HttpMethod.GET, makeAuthEntity(), User.class);
            user = entity.getBody();
        }catch(RestClientResponseException e){
            System.err.println("Error when retrieving user by username.");
            BasicLogger.log(e.getMessage());
        }
        return user;
    }
//get users by account id.
    @Override
    public User getUserByAccountId(int accountId) {
        User user = null;
        try{
            HttpEntity<User> entity = restTemplate.exchange(API_BASE_URL + "accountId/" + accountId, HttpMethod.GET, makeAuthEntity(), User.class);
            user = entity.getBody();
        }catch(RestClientResponseException e){
            System.err.println("Error when retrieving user.");
            BasicLogger.log(e.getMessage());
        }
        return user;
    }
//create a http entity object method.
    private HttpEntity<Void> makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }
}
