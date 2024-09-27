package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class JdbcAccountDao implements AccountDao{
    

    //create a no args constructor.
    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
//find by all accounts
    @Override
    public List<Account> findAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT account_id, user_id, balance FROM account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while(results.next()){
                accounts.add(mapToAccount(results));
            }
        return accounts;
    }
//find accounts by userId
    @Override
    public Account findAccountByUserId(int userId) {
        Account account = null;
        String sql = "select user_id, account_id, balance from account where user_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);

        if (results.next()){
            account = mapToAccount(results);
        }
        return account;
    }
//find accounts by usernames
    @Override
    public Account findAccountsByUsername(String username) {
        Account account = null;
        String sql = "select user_id, account_id, balance from account JOIN tenmo_user USING(user_id) WHERE username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);

        if (results.next()){
            account = mapToAccount(results);
        }
        return account;
    }
//find accounts by ids.
    @Override
    public Account findByAccountId(int accountId) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account where account_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if(results.next()){
            account = mapToAccount(results);
        }
        return account;
    }
//create a mapToAccount for a helper method.
    private Account mapToAccount(SqlRowSet sqlRowSet){
        Account account = new Account();
        account.setBalance(sqlRowSet.getBigDecimal("balance"));
        account.setUserID(sqlRowSet.getInt("user_id"));
        account.setAccountId(sqlRowSet.getInt("account_id"));
        return account;
    }
}
