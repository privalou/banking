package io.privalou.repository;

import com.google.inject.Singleton;
import org.jooq.Configuration;
import org.jooq.impl.DSL;

import java.math.BigDecimal;

import static banking.tables.Account.ACCOUNT;

@Singleton
public class AccountRepository {

    public Long createAccount(BigDecimal amount, Configuration configuration) {
        return DSL.using(configuration).insertInto(ACCOUNT)
                .columns(ACCOUNT.BALANCE)
                .values(amount)
                .returning(ACCOUNT.ID)
                .fetchOne()
                .getId();
    }

    public BigDecimal getBalanceByAccountId(Long id, Configuration configuration) {
        return DSL.using(configuration).select(ACCOUNT.BALANCE)
                .from(ACCOUNT)
                .where(ACCOUNT.ID.eq(id))
                .fetchOne()
                .component1();
    }

    public void updateBalance(Long id, BigDecimal amount, Configuration configuration) {
        DSL.using(configuration).update(ACCOUNT)
                .set(ACCOUNT.BALANCE, amount)
                .where(ACCOUNT.ID.eq(id))
                .execute();
    }
}
