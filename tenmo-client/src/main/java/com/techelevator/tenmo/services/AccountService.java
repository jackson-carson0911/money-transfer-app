package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AccountDto;

import java.math.BigDecimal;
import java.util.Map;

public interface AccountService {
    
    Account[] findAccounts();
    Account findAccountByUserId(int userId);


    void setToken(String token);
}
