package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Transfer;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class JdbcTransferDao implements TransferDao{

private final JdbcTemplate jdbcTemplate;
//create variables for the numbers in the app for pending, approved, rejected, etc.
    private final int PENDING = 1;
    private final int APPROVED = 2;
    private final int REJECTED = 3;
    private final int REQUEST = 1;
    private final int SEND = 2;
//create variables for sql statements to make code look cleaner.
    private String updateTransferStatusSql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?;";
    private String updateBalanceSql = "UPDATE account SET balance = ? WHERE account_id = ?";
    private String createTransferSql ="INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?, ?, ?, ?, ?) returning transfer_id;";
    private String getAccountById = "SELECT balance FROM account WHERE account_id = ?;";
    private String joinedTransferSql = "SELECT t.transfer_id, t.amount, t.transfer_type_id, " + 
                        "t.transfer_status_id, t.account_from, " + 
                        "f.user_id AS from_user_id, q.username AS from_username, " + 
                        "f.balance AS from_balance, t.account_to, " + 
                        "o.user_id AS to_user_id, w.username AS to_username, o.balance AS to_balance " + 
                        "FROM transfer t " + 
                        "JOIN account f ON f.account_id = t.account_from " + 
                        "JOIN account o ON o.account_id = t.account_to " + 
                        "JOIN tenmo_user q on q.user_id = f.user_id " + 
                        "JOIN tenmo_user w on w.user_id = o.user_id ";
//create a no args constructor.
    public JdbcTransferDao(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }
//find all transfers.
    @Override
    public List<Transfer> findAllTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        SqlRowSet results = jdbcTemplate.queryForRowSet(joinedTransferSql);
    
        while(results.next()){
    
            transfers.add(mapToTransfer(results));
        }
        return transfers;
    }
//find transfers by id.
    @Override
    public Transfer findById(int transferId){
        Transfer transfer = null;
        String sql = joinedTransferSql + "WHERE t.transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);

        if(results.next()){
            transfer = mapToTransfer(results);
        }
        return transfer;
    }
//create a request method for transfers.
    @Override
    public Transfer createRequest(Transfer transfer){
        Transfer newTransfer = null;
        if (transfer.getTransferTypeId() == REQUEST) {
            try {
                int newTransferId = jdbcTemplate.queryForObject(createTransferSql, int.class, REQUEST,
                                    PENDING, transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
                newTransfer = findById(newTransferId);
            } catch (CannotGetJdbcConnectionException e){
                System.out.println("Cannot connect to database!");
            }catch (DataIntegrityViolationException e){
                System.out.println("Data integrity violation!");
            }
        }else{
            try {
                int newTransferId = jdbcTemplate.queryForObject(createTransferSql, int.class, SEND,
                                    transfer.getTransferStatusId(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
                approve(findById(newTransferId));
                newTransfer = findById(newTransferId);
            }catch (CannotGetJdbcConnectionException e){
                System.out.println("Cannot connect to database!");
            }catch (DataIntegrityViolationException e){
                System.out.println("Data integrity violation!");
            }
        } return newTransfer;

    }

//create an approved method for transfer.
    @Override
    public Transfer approve(Transfer transfer) {
        Transfer updated = null;

        try {
            int numberOfRows = jdbcTemplate.update(updateTransferStatusSql, APPROVED, transfer.getTransferId());
            if (numberOfRows == 0) {
                throw new DaoException("Your updates to the transfer could not be processed!");
            }
            updated = findById(transfer.getTransferId());
            try{
                BigDecimal fromAmount = jdbcTemplate.queryForObject(getAccountById, BigDecimal.class, updated.getAccountFrom());
                BigDecimal toAmount = jdbcTemplate.queryForObject(getAccountById, BigDecimal.class, updated.getAccountTo() );
                int numberOfWithdrawnAccounts = jdbcTemplate.update(updateBalanceSql, fromAmount.subtract(updated.getAmount()), updated.getAccountFrom());
                int numberOfDepositedAccounts = jdbcTemplate.update(updateBalanceSql, toAmount.add(updated.getAmount()), updated.getAccountTo());
                if(numberOfWithdrawnAccounts == 0 || numberOfDepositedAccounts == 0) {
                    throw new DaoException("Your request could not be withdrawn.");
                }
            }
            catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to database!", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity Violation!", e);
        }
            return findById(transfer.getTransferId());
        }catch (CannotGetJdbcConnectionException e){
            throw new DaoException("Unable to connect to database!", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data Integrity Violation!", e);
        }
    }
//create a decline method for transfers.
    @Override
    public Transfer decline(Transfer transfer){
        Transfer updated = null;
        try {
            int numberOfRows = jdbcTemplate.update(updateTransferStatusSql, REJECTED, transfer.getTransferId());
            if (numberOfRows == 0) {
                throw new DaoException("Your updates to the transfer could not be processed!");
            }
            updated = findById(transfer.getTransferId());
            return updated;
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to database!", e);
        } catch (DataIntegrityViolationException e) {
           throw new DaoException("Data Integrity Violation!", e);
        }
    }

//create a mapToTransfer for a helper method.
    private Transfer mapToTransfer(SqlRowSet sqlRowSet){
        Transfer transfer = new Transfer();
        transfer.setTransferId(sqlRowSet.getInt("transfer_id"));
        transfer.setTransferTypeId(sqlRowSet.getInt("transfer_type_id"));
        transfer.setTransferStatusId(sqlRowSet.getInt("transfer_status_id"));
        transfer.setAccountFrom(sqlRowSet.getInt("account_from"));
        transfer.setAccountTo(sqlRowSet.getInt("account_to"));
        transfer.setAmount(sqlRowSet.getBigDecimal("amount"));
        transfer.setFromUserId(sqlRowSet.getInt("from_user_id"));
        transfer.setFromUsername(sqlRowSet.getString("from_username"));
        transfer.setFromBalance(sqlRowSet.getBigDecimal("from_balance"));
        transfer.setToUserId(sqlRowSet.getInt("to_user_id"));
        transfer.setToUsername(sqlRowSet.getString("to_username"));
        transfer.setToBalance(sqlRowSet.getBigDecimal("to_balance"));
        return transfer;
    }
}
