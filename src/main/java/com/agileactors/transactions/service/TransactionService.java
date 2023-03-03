package com.agileactors.transactions.service;

import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.entity.Currency;
import com.agileactors.transactions.entity.TransactionEntity;
import com.agileactors.transactions.exception.AccountNotFoundException;
import com.agileactors.transactions.exception.NotSufficientBalanceException;
import com.agileactors.transactions.exception.TransferBetweenSameAccountsException;
import com.agileactors.transactions.repository.AccountRepository;
import com.agileactors.transactions.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final String SOURCE_ACCOUNT_ERROR_MSG = "Source account does not exist.";
    private static final String TARGET_ACCOUNT_ERROR_MSG = "Target account does not exist.";
    private static final String OPERATION_NOT_ALLOWED_ERROR_MSG = "This operation is not allowed on the same accounts.";
    private static final String NOT_SUFFICIENT_BALANCE_ERROR_MSG = "The balance of the source account is less from the transaction amount.";

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionEntity create(final String sourceAccountId, final String targetAccountId, final BigDecimal amount, Optional<Currency> currency) throws AccountNotFoundException, TransferBetweenSameAccountsException, NotSufficientBalanceException {
        if (StringUtils.isEmpty(sourceAccountId)) {
            throw new AccountNotFoundException(SOURCE_ACCOUNT_ERROR_MSG);
        }

        if (StringUtils.isEmpty(targetAccountId)) {
            throw new AccountNotFoundException(TARGET_ACCOUNT_ERROR_MSG);
        }

        Optional<AccountEntity> sourceAccount = accountRepository.findById(sourceAccountId);
        Optional<AccountEntity> targetAccount = accountRepository.findById(targetAccountId);

        if (sourceAccount.isEmpty()) {
            throw new AccountNotFoundException(SOURCE_ACCOUNT_ERROR_MSG);
        }

        if (targetAccount.isEmpty()) {
            throw new AccountNotFoundException(TARGET_ACCOUNT_ERROR_MSG);
        }

        if (StringUtils.equals(sourceAccountId, targetAccountId)) {
            throw new TransferBetweenSameAccountsException(OPERATION_NOT_ALLOWED_ERROR_MSG);
        }

        AccountEntity sourceAccountEntity = sourceAccount.get();
        if (sourceAccountEntity.getBalance().compareTo(amount) < 0) {
            throw new NotSufficientBalanceException(NOT_SUFFICIENT_BALANCE_ERROR_MSG);
        }

        // Save the new transaction
        TransactionEntity entity = new TransactionEntity();
        entity.setId(UUID.randomUUID().toString());
        entity.setSourceAccount(sourceAccount.get());
        entity.setTargetAccount(targetAccount.get());
        entity.setAmount(amount);
        currency.ifPresent(entity::setCurrency);
        TransactionEntity savedEntity = transactionRepository.save(entity);

        // Update the source account
        BigDecimal sourceNewBalance = sourceAccountEntity.getBalance().subtract(amount);
        sourceAccountEntity.setBalance(sourceNewBalance);
        accountRepository.save(sourceAccountEntity);

        // Update the target account
        BigDecimal targetNewBalance = targetAccount.get().getBalance().add(amount);
        targetAccount.get().setBalance(targetNewBalance);
        accountRepository.save(targetAccount.get());

        return savedEntity;
    }

    public Iterable<TransactionEntity> findAllTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<TransactionEntity> findTransactionById(final String id) {
        return transactionRepository.findById(id);
    }

    public void saveAllTransactions(List<AccountEntity> entities) {
        accountRepository.saveAll(entities);
    }

}
