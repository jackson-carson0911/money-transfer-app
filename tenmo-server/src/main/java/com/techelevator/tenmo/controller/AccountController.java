package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AccountDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;



@RestController
@RequestMapping("accounts")
@PreAuthorize("isAuthenticated()")
public class  AccountController {

    

    private final AccountDao accountDao;
    private final UserDao user;

    
    @Autowired
    public AccountController(AccountDao accountDao, UserDao userDao){
        this.accountDao = accountDao;
        this.user = userDao;
    }
    
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Account> getAccount(){
        List<Account> accounts = accountDao.findAccounts();
        AccountDto accountDto;
        Map<Integer,AccountDto> accountsMap = new HashMap<>();
            if (accountDao == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account(s) Not Found!");
            }
        return accounts;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(path = "/{userId}", method = RequestMethod.GET)
    public Account getAccountByUserId(@PathVariable int userId, Principal principal) {
        if (!user.getUserByUsername(principal.getName()).equals(user.getUserById(userId))) {
            
            throw new ResponseStatusException (HttpStatus.FORBIDDEN, "The logged in user isn't allowed to perform " + 
                    "this action because they don't have permission.",null);
        }
        
        return accountDao.findAccountByUserId(userId);  
    }

    public AccountDto mapAccountDto(Account account){
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountId(account.getAccountId());
        accountDto.setUserId(account.getUserId());
        return accountDto;
    }

}
