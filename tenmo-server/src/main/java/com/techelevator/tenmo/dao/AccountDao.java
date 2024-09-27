package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface AccountDao{

    List<Account> findAccounts();
    Account findAccountByUserId(int userId);
    Account findAccountsByUsername(String username);
    Account findByAccountId(int accountId);
}
