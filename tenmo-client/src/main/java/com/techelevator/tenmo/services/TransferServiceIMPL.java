package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Service
public class TransferServiceIMPL implements TransferService {

    public static final String API_BASE_URL = "http://localhost:8080/transfer/";
    private final RestTemplate restTemplate = new RestTemplate();
    private String token = null;

    public void setToken(String token) {
        this.token = token;
    }

    public TransferServiceIMPL(){
    }
//find all transfers.
    @Override
    public Transfer[] findAll() {
        Transfer[] transfers = null;
        try{
            HttpEntity<Void> entity = makeAuthEntity();
            ResponseEntity<Transfer[]> response = restTemplate.exchange(API_BASE_URL, HttpMethod.GET, entity, Transfer[].class);
            transfers = response.getBody();
        }catch (RestClientResponseException e){
            System.err.println("Error when retrieving all transfers.");
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }
//find transfers by id.
    @Override
    public Transfer findByTransferId(int transferId) {
        Transfer transfer = null;
        try{
            ResponseEntity<Transfer> response = restTemplate.exchange(API_BASE_URL + transferId, HttpMethod.GET, makeAuthEntity(), Transfer.class);
            transfer = response.getBody();
        }catch(RestClientResponseException e){
            System.err.println("Error when retrieving transfers by ID.");
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }
//create a request for transfers.
    @Override
    public Transfer createRequest(Transfer transfer) {
        Transfer returnedTransfer = null;
        try{
            HttpEntity<Transfer> entity = makeTransferEntity(transfer);
            returnedTransfer = restTemplate.postForObject(API_BASE_URL + "request", entity, Transfer.class);
        }catch(RestClientResponseException e){
            System.err.println("Error when creating transfer request.");
            BasicLogger.log(e.getMessage());
        }
        return returnedTransfer;
    }
//create an approved method for transfers.
    @Override
    public Transfer approve(Transfer transfer) {
        Transfer approvedTransfer = null;
        try{
            HttpEntity<Transfer> entity = makeTransferEntity(transfer);
            restTemplate.put(API_BASE_URL + "approve", entity);
            approvedTransfer = findByTransferId(transfer.getTransferId());
        }catch(RestClientResponseException e){
            System.err.println("Error when approving transfer.");
            BasicLogger.log(e.getMessage());
        }
        return approvedTransfer;
    }
//create a decline method for transfers.
    @Override
    public Transfer decline(Transfer transfer) {
        Transfer declinedTransfer = null;
        try{
            HttpEntity<Transfer> entity = makeTransferEntity(transfer);
            restTemplate.put(API_BASE_URL + "decline", entity);
            declinedTransfer = findByTransferId(transfer.getTransferId());
        }catch(RestClientResponseException e){
            System.err.println("Error when declining transfer.");
            BasicLogger.log(e.getMessage());
        }
        return declinedTransfer;
    }
//create a http entity object method.
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = createHeaders();
        return new HttpEntity<>(headers);
    }
//create a http entity transfer method.
    private HttpEntity<Transfer> makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = createHeaders();
        return new HttpEntity<>(transfer, headers);
    }
//create a http headers object method.
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }
}
