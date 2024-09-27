package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcTransferDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class JdbcTransferDaoTests extends BaseDaoTests {

    private JdbcTransferDao sut;

    private static final Transfer TRANSFER_1 = new Transfer(1, 1, 2001, 2002, new BigDecimal("100.00"));
    private static final Transfer TRANSFER_2 = new Transfer(1, 1, 2002, 2003, new BigDecimal("50.00"));
    private static final Transfer TRANSFER_3 = new Transfer(1, 1, 2003, 2001, new BigDecimal("50.00"));

    @Before
    public void setup() {
        sut = new JdbcTransferDao(dataSource);
    }

    @Test
    public void findAllTransfers_returns_all_transfers() {
       Assert.assertNotNull(sut.findAllTransfers());
    }

    @Test
    public void findById_returns_correct_transfer() {
        Transfer createdRequest = sut.createRequest(TRANSFER_1);
        Transfer result = sut.findById(createdRequest.getTransferId());
            assertNotNull(result);
    }

    @Test
    public void findById_returns_null_for_invalid_id() {
        Transfer transfer = sut.findById(-1);

        Assert.assertNull(transfer);
    }

    @Test
    public void createRequest_creates_transfer() {
        Transfer newTransfer = new Transfer();
        newTransfer.setTransferTypeId(1);
        newTransfer.setTransferStatusId(1);
        newTransfer.setAccountFrom(2002);
        newTransfer.setAccountTo(2001);
        newTransfer.setAmount(new BigDecimal(100.00));
        Transfer createdTransfer = sut.createRequest(newTransfer);
        Assert.assertNotEquals(0,createdTransfer.getTransferId());
        Assert.assertEquals(TRANSFER_1.getTransferTypeId(), createdTransfer.getTransferTypeId());
        Assert.assertEquals(1, createdTransfer.getTransferStatusId());
        Assert.assertEquals(TRANSFER_1.getAccountFrom(), createdTransfer.getAccountFrom());
        Assert.assertEquals(TRANSFER_1.getAccountTo(), createdTransfer.getAccountTo());
        Assert.assertEquals(TRANSFER_1.getAmount(), createdTransfer.getAmount());
    }

    @Test
    public void approve_updates_transfer_status_and_balance() {
        Transfer createdRequest = sut.createRequest(TRANSFER_2);
        Transfer actual = sut.approve(createdRequest);
        Assert.assertNotEquals(0,actual.getTransferId());
        Assert.assertEquals(TRANSFER_2.getTransferTypeId(), actual.getTransferTypeId());
        Assert.assertEquals(2, actual.getTransferStatusId());
        Assert.assertEquals(TRANSFER_2.getAccountFrom(), actual.getAccountFrom());
        Assert.assertEquals(TRANSFER_2.getAccountTo(), actual.getAccountTo());
        Assert.assertEquals(TRANSFER_2.getAmount(), actual.getAmount());
    }

    @Test(expected = DaoException.class)
    public void decline_updates_transfer_status() {
        sut.decline(TRANSFER_3);
    }
}