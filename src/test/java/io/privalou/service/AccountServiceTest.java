package io.privalou.service;

import io.privalou.repository.AccountRepository;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    private static final BigDecimal BALANCE = new BigDecimal("100.00");

    private static final Long ACCOUNT_ID = 1L;

    @BeforeEach
    public void before() {
        DSLContext dslContext = DSL.using(new MockConnection(c -> new MockResult[]{}), SQLDialect.H2);
        accountService = new AccountService(accountRepository, dslContext);
    }

    @Test
    public void testSuccessfulAccountCreation() {
        when(accountRepository.createAccount(any(), any())).thenReturn(ACCOUNT_ID);
        Long accountId = accountService.createAccount(BALANCE);
        verify(accountRepository, times(1)).createAccount(eq(BALANCE), any());
        Assertions.assertEquals(ACCOUNT_ID, accountId);
    }

    @Test
    public void testSuccessfulBalanceObtaining() {
        when(accountRepository.getBalanceByAccountId(eq(ACCOUNT_ID), any())).thenReturn(BALANCE);
        BigDecimal balanceByAccountId = accountService.getBalanceByAccountId(ACCOUNT_ID);
        verify(accountRepository, times(1)).getBalanceByAccountId(eq(ACCOUNT_ID), any());
        Assertions.assertEquals(BALANCE, balanceByAccountId);
    }
}
