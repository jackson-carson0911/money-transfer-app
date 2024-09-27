package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;

public class JdbcAccountDaoTests extends BaseDaoTests {

    private static final Account ACCOUNT_1 = new Account(2001, 1001, new BigDecimal("1000.00"));
    private static final Account ACCOUNT_2 = new Account(2002, 1002, new BigDecimal("500.00"));
    private static final Account ACCOUNT_3 = new Account(2003, 1003, new BigDecimal("750.00"));

    private JdbcAccountDao sut;

    @Before
    public void setup() {
        sut = new JdbcAccountDao(dataSource);
    }

    @Test
    public void findAccounts_returns_all_accounts() {
        List<Account> accounts = sut.findAccounts();
        Assert.assertNotNull(accounts);
    }

    @Test
    public void findAccountByUserId_returns_correct_account() {
        Account account = sut.findAccountByUserId(ACCOUNT_1.getUserId());
        Assert.assertEquals(ACCOUNT_1.getAccountId(), account.getAccountId());
        Assert.assertEquals(ACCOUNT_1.getUserId(), account.getUserId());
        Assert.assertEquals(ACCOUNT_1.getBalance(), account.getBalance());
    }

    @Test
    public void findAccountByUserId_returns_null_for_invalid_id() {
        Account account = sut.findAccountByUserId(-1);

        Assert.assertNull(account);
    }

    @Test
    public void findAccountsByUsername_returns_correct_account() {
        Account account = sut.findAccountsByUsername("user1");
        Assert.assertEquals(ACCOUNT_1.getAccountId(), account.getAccountId());
        Assert.assertEquals(ACCOUNT_1.getUserId(), account.getUserId());
        Assert.assertEquals(ACCOUNT_1.getBalance(), account.getBalance());
    }

    @Test
    public void findAccountsByUsername_returns_null_for_invalid_username() {
        Account account = sut.findAccountsByUsername("invalid_user");

        Assert.assertNull(account);
    }

    @Test
    public void findByAccountId_returns_correct_account() {
        Account account = sut.findByAccountId(ACCOUNT_1.getAccountId());
        Assert.assertEquals(ACCOUNT_1.getAccountId(), account.getAccountId());
        Assert.assertEquals(ACCOUNT_1.getUserId(), account.getUserId());
        Assert.assertEquals(ACCOUNT_1.getBalance(), account.getBalance());
    }

    @Test
    public void findByAccountId_returns_null_for_invalid_id() {
        Account account = sut.findByAccountId(-1);

        Assert.assertNull(account);
    }
}