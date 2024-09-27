package com.techelevator.tenmo.model;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

//create a constructor, getters, setters and a no args constructor for the accountDTO to be called on in client side.

public class AccountDto{

private int accountId;
private int userId;
@Positive
private BigDecimal balance;

    public AccountDto(int accountId, int userID) {
        this.accountId = accountId;
        this.userId = userID;
    }
    public AccountDto(){
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

    public void setUserId(int userID) {
        this.userId = userID;
    }

}
