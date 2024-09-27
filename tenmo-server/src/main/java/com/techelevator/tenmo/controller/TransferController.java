package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("transfer")
@PreAuthorize("isAuthenticated()")
public class TransferController{

    private final TransferDao transferDao;
    private final UserDao user;

    @Autowired
    public TransferController(TransferDao transferDao, UserDao userDao){
        this.transferDao = transferDao;
        this.user = userDao;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("")
    public List<TransferDto> findAllTransfers(Transfer transfer){
        List<TransferDto> transferDtos = new ArrayList<>();
        List<Transfer> transfers = transferDao.findAllTransfers();
            for (Transfer t : transfers) {
                transferDtos.add(mapTransferDto(t));
            }
        return transferDtos;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{transferId}")
    public TransferDto findById(@PathVariable int transferId, Principal principal){
        if (!(user.getUserByUsername(principal.getName()).getId() != user.getUserByAccountId(transferDao.findById(transferId).getAccountFrom()).getId() ^ 
                user.getUserByUsername(principal.getName()).getId() != user.getUserByAccountId(transferDao.findById(transferId).getAccountTo()).getId())) {
              //throws 403 forbidden when the logged in user is not involved in the transaction requested in the endpoint
                throw new ResponseStatusException (HttpStatus.FORBIDDEN, "The logged in user isn't allowed to perform " + 
                    "this action because they don't have permission.",null);
        }
        return mapTransferDto(transferDao.findById(transferId));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/request")
    public TransferDto createRequest(@Valid @RequestBody Transfer transfer){
        return mapTransferDto(transferDao.createRequest(transfer));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/approve")
    public TransferDto approve(@Valid @RequestBody Transfer transfer){
        return mapTransferDto(transferDao.approve(transfer));
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping("/decline")
    public TransferDto decline(@Valid @RequestBody Transfer transfer){
        return mapTransferDto(transferDao.decline(transfer));
    }


    public TransferDto mapTransferDto(Transfer transfer){
        TransferDto transferDto = new TransferDto();
        transferDto.setTransferId(transfer.getTransferId());
        transferDto.setTransferTypeId(transfer.getTransferTypeId());
        transferDto.setTransferStatusId(transfer.getTransferStatusId());
        transferDto.setAccountFrom(transfer.getAccountFrom());
        transferDto.setFromUserId(transfer.getFromUserId());
        transferDto.setFromUsername(transfer.getFromUsername());
        transferDto.setAccountTo(transfer.getAccountTo());
        transferDto.setToUserId(transfer.getToUserId());
        transferDto.setToUsername(transfer.getToUsername());
        transferDto.setAmount(transfer.getAmount());
        return transferDto;
    }








}
