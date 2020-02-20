package io.privalou.service;

import io.privalou.exception.TransferException;
import io.privalou.model.TransferModel;
import io.privalou.repository.AccountRepository;
import io.privalou.repository.TransferRepository;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Singleton
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final DSLContext dslContext;

    @Inject
    public TransferService(AccountRepository accountRepository,
                           TransferRepository transferRepository,
                           DSLContext dslContext) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.dslContext = dslContext;
    }

    public void executeMoneyTransfer(TransferModel transfer) {
        log.debug("Transfer to be proceed: {}", transfer);
        try {
            dslContext.transaction(config -> {
                if (transfer.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                    throw new TransferException("Deposit can not be negative");
                }
                BigDecimal senderBalance = accountRepository.getBalanceByAccountId(transfer.getSenderId(), config);
                if (senderBalance.compareTo(transfer.getAmount()) < 0) {
                    throw new TransferException("Insufficient funds in the account");
                }
                BigDecimal receiverBalance = accountRepository.getBalanceByAccountId(transfer.getReceiverId(), config);
                accountRepository.updateBalance(transfer.getSenderId(),
                        senderBalance.subtract(transfer.getAmount()),
                        config);
                accountRepository.updateBalance(transfer.getReceiverId(),
                        receiverBalance.add(transfer.getAmount()),
                        config);
                transferRepository.createTransfer(transfer.getSenderId(),
                        transfer.getReceiverId(),
                        transfer.getAmount(),
                        config);
            });
            log.debug("Money transfer has been executed: {}", transfer);
        } catch (Exception e) {
            String msg = "Error occurred during money transaction";
            log.error(msg, e);
            throw new TransferException(msg);
        }
    }

    public List<TransferModel> getTransfersByAccountId(Long accountId) {
        log.debug("Obtaining transfers for account with id: {}", accountId);
        List<TransferModel> transferModels = transferRepository
                .getTransfersByAccountId(accountId, dslContext.configuration());
        log.debug("Obtained transfers {} for account with id: {}", transferModels.size(), accountId);
        return transferModels;
    }
}
