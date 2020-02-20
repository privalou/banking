package io.privalou.repository;

import io.privalou.model.TransferModel;
import org.jooq.Configuration;
import org.jooq.impl.DSL;

import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

import static banking.tables.Account.*;
import static banking.tables.Transfer.TRANSFER;

@Singleton
public class TransferRepository {

    public void createTransfer(Long senderId, Long receiverId, BigDecimal amount, Configuration configuration) {
        DSL.using(configuration).insertInto(TRANSFER)
                .columns(TRANSFER.SENDER_ID, TRANSFER.RECEIVER_ID, TRANSFER.AMOUNT)
                .values(senderId, receiverId, amount)
                .execute();
    }

    public List<TransferModel> getTransfersByAccountId(Long accountId, Configuration config) {
        return DSL.using(config).select(TRANSFER.SENDER_ID, TRANSFER.RECEIVER_ID, TRANSFER.AMOUNT)
                .from(ACCOUNT)
                .join(TRANSFER)
                .on(ACCOUNT.ID.eq(TRANSFER.SENDER_ID).or(ACCOUNT.ID.eq(TRANSFER.SENDER_ID)))
                .where(ACCOUNT.ID.eq(accountId))
                .fetch(record -> new TransferModel(record.component1(), record.component2(), record.component3()));
    }
}
