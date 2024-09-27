package com.techelevator.tenmo.model;


import javax.validation.constraints.Positive;
import java.math.BigDecimal;
//create a constructor, getters, setters and a no args constructor for the transfer DTO to be called on.
public class TransferDto {
    private int transferId;
    private int transferTypeId;
    private int transferStatusId;
    private int accountFrom;
    private String fromUsername;
    private int fromUserId;
    private int accountTo;
    private int toUserId;
    private String toUsername;
    @Positive
    private BigDecimal amount;




    public TransferDto(int transferId, int transferTypeId, int transferStatusId, int accountTo,
     String toUsername, int toUserId, int accountFrom, String fromUsername, int fromUserId, BigDecimal amount) {
        this.transferId = transferId;
        this.transferTypeId = transferTypeId;
        this.transferStatusId = transferStatusId;
        this.accountTo = accountTo;
        this.fromUsername = fromUsername;
        this.toUsername = toUsername;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.accountFrom = accountFrom;
        this.amount = amount;
    }

    public TransferDto(){
    }

    public int getTransferId() {
        return transferId;
    }

    public void setTransferId(int transferId) {
        this.transferId = transferId;
    }

    public int getTransferTypeId() {
        return transferTypeId;
    }

    public void setTransferTypeId(int transferTypeId) {
        this.transferTypeId = transferTypeId;
    }

    public int getTransferStatusId() {
        return transferStatusId;
    }

    public void setTransferStatusId(int transferStatusId) {
        this.transferStatusId = transferStatusId;
    }

    public int getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(int accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(int accountTo) {
        this.accountTo = accountTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }
    
    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

}
