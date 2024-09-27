package com.techelevator.tenmo.model;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;
//create a constructor, getters, setters and a no args constructor for the account model to be called on in client side.
public class Account {

    private int accountId;
    private int userId;
    @Positive
    private BigDecimal balance;

    public Account(int id, int userId, BigDecimal balance) {
            this.accountId = id;
            this.userId = userId;
            this.balance = balance;
        }
    public Account(){
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
        }
    }

