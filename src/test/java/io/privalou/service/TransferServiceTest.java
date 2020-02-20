package io.privalou.service;


import io.privalou.exception.TransferException;
import io.privalou.model.TransferModel;
import io.privalou.repository.AccountRepository;
import io.privalou.repository.TransferRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private AccountRepository accountRepository;

    private TransferService transferService;

    private static final Long FIRST_ACCOUNT_ID = 1L;
    private static final Long SECOND_ACCOUNT_ID = 2L;

    private static final BigDecimal TEN = BigDecimal.valueOf(10);
    private static final BigDecimal FIFTY = BigDecimal.valueOf(50);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @BeforeEach
    public void before() {
        DSLContext dslContext = DSL.using(new MockConnection(c -> new MockResult[]{}), SQLDialect.H2);
        transferService = new TransferService(accountRepository, transferRepository, dslContext);
    }

    @Test
    public void failingMoneyTransferWithNullBalanceOfSender() {
        Assertions.assertThrows(TransferException.class, () -> transferService.executeMoneyTransfer(
                new TransferModel(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, FIFTY)));
    }

    @Test
    public void failingMoneyTransferWithTransferAmountBiggerThanAccountBalance() {
        when(accountRepository.getBalanceByAccountId(eq(FIRST_ACCOUNT_ID), any())).thenReturn(TEN);

        Assertions.assertThrows(TransferException.class, () -> transferService.executeMoneyTransfer(
                new TransferModel(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, FIFTY)));
    }

    @Test
    public void failingMoneyTransferWithNullBalanceOfReceiver() {
        when(accountRepository.getBalanceByAccountId(eq(FIRST_ACCOUNT_ID), any())).thenReturn(HUNDRED);

        Assertions.assertThrows(TransferException.class, () -> transferService.executeMoneyTransfer(
                new TransferModel(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, FIFTY)));
    }

    @Test
    public void failingMoneyTransferWithNegativeTransferAmount() {
        final TransferModel transfer = new TransferModel(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, FIFTY.negate());
        Assertions.assertThrows(TransferException.class, () -> transferService.executeMoneyTransfer(transfer));
    }

    @Test
    public void testSuccessfulFlowOfMoneyTransferring() {
        when(accountRepository.getBalanceByAccountId(eq(FIRST_ACCOUNT_ID), any())).thenReturn(HUNDRED);
        when(accountRepository.getBalanceByAccountId(eq(SECOND_ACCOUNT_ID), any())).thenReturn(TEN);

        final TransferModel transfer = new TransferModel(FIRST_ACCOUNT_ID, SECOND_ACCOUNT_ID, FIFTY);
        transferService.executeMoneyTransfer(transfer);

        verify(accountRepository).updateBalance(eq(FIRST_ACCOUNT_ID), eq(HUNDRED.subtract(FIFTY)), any());
        verify(accountRepository).updateBalance(eq(SECOND_ACCOUNT_ID), eq(TEN.add(FIFTY)), any());
        verify(transferRepository).createTransfer(eq(transfer.getSenderId()),
                eq(transfer.getReceiverId()),
                eq(transfer.getAmount()),
                any());
    }
}
