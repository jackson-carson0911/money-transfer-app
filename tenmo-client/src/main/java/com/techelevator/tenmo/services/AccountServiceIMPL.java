package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AccountDto;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.HashMap;

@Service
public class AccountServiceIMPL implements AccountService {

    public static final String API_BASE_URL = "http://localhost:8080/accounts/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String token = null;

    public void setToken(String token) {
        this.token = token;
    }

    public AccountServiceIMPL() {
    }
//find all accounts.
    public Account[] findAccounts(){
        Account[] accounts = null;
        AccountDto accountDto = new AccountDto();
        Map<Integer,AccountDto> accountMap = new HashMap<>();
        try{
            HttpEntity<Account[]> entity = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, makeEntity(), Account[].class);
            accounts = entity.getBody();

        }catch (RestClientResponseException e){
            System.err.println("Error when retrieving all accounts.");
            BasicLogger.log(e.getMessage());
        }
        return accounts;
    }
//find accounts by user id.
    public Account findAccountByUserId(int userId){
        Account account = null;
        try{
            HttpEntity<Account> entity = restTemplate.exchange(API_BASE_URL + userId, HttpMethod.GET, makeAuthEntity(), Account.class);
            account = entity.getBody();
        }catch(RestClientResponseException e){
            System.err.println("Error when retrieving account. ");
            BasicLogger.log(e.getMessage());
        }
        return account;
    }
//create a http entity object method.
    private HttpEntity<Void> makeAuthEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }
//create a http entity object method to include headers and a body.
    private HttpEntity<Account[]> makeEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return new HttpEntity<>(headers);
    }
}