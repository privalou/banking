package io.privalou.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.privalou.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;

import java.math.BigDecimal;

@Slf4j
@Singleton
public class AccountService {

    private final AccountRepository accountRepository;
    private final DSLContext dslContext;

    @Inject
    public AccountService(AccountRepository accountRepository,
                          DSLContext dslContext) {
        this.accountRepository = accountRepository;
        this.dslContext = dslContext;
    }

    public Long createAccount(BigDecimal balance) {
        log.debug("Creating account with balance {}", balance);
        Long createdAccountId = dslContext
                .transactionResult(configuration -> accountRepository.createAccount(balance, configuration));
        log.debug("Creatded account with id {} and balance {}", createdAccountId, balance);
        return createdAccountId;
    }

    public BigDecimal getBalanceByAccountId(Long accountId) {
        log.debug("Obtaining balance of account with id {}", accountId);
        BigDecimal balance = dslContext.transactionResult(configuration ->
                accountRepository.getBalanceByAccountId(accountId, configuration));
        log.debug("Account with id {} has balance: {}", accountId, balance);
        return balance;
    }

}
