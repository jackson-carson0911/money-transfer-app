package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferDao {

    List<Transfer> findAllTransfers();
    Transfer findById(int transferId);
    Transfer createRequest(Transfer transfer);
    Transfer approve(Transfer transfer);
    Transfer decline(Transfer transfer);

}
