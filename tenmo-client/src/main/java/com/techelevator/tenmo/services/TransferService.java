package com.techelevator.tenmo.services;

import java.util.List;

import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.model.Transfer;


public interface TransferService {

    Transfer[] findAll();
    Transfer findByTransferId(int transferId);
    Transfer createRequest(Transfer transfer);
    Transfer approve(Transfer transfer);
    Transfer decline(Transfer transfer);

    void setToken(String token);


}
