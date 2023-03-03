package com.agileactors.transactions.repository;

import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.entity.Currency;
import com.agileactors.transactions.entity.TransactionEntity;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static com.agileactors.transactions.entity.Currency.GBP;
import static com.agileactors.transactions.entity.Currency.USD;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRepositoryTest {

    private final String PRESAVED_ACCOUNT_ID_1 = UUID.randomUUID().toString();
    private final String PRESAVED_ACCOUNT_ID_2 = UUID.randomUUID().toString();
    private final String PRESAVED_TRANSACTION_ID = UUID.randomUUID().toString();

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeAll
    void setUp() {
        AccountEntity presavedEntity1 = getAccountEntity(PRESAVED_ACCOUNT_ID_1, GBP, BigDecimal.valueOf(100d));
        AccountEntity presavedEntity2 = getAccountEntity(PRESAVED_ACCOUNT_ID_2, USD, BigDecimal.valueOf(100d));
        accountRepository.saveAll(Arrays.asList(presavedEntity1, presavedEntity2));

        TransactionEntity transactionEntity = getTransactionEntity(PRESAVED_TRANSACTION_ID, USD, BigDecimal.TEN, presavedEntity1, presavedEntity2);
        transactionRepository.save(transactionEntity);
    }

    @Test
    void itShouldFindTransactionEntityById() {
        Optional<TransactionEntity> savedEntityOpt = transactionRepository.findById(PRESAVED_TRANSACTION_ID);
        assertTrue(savedEntityOpt.isPresent());
    }

    private AccountEntity getAccountEntity(final String id, final Currency currency, final BigDecimal balance) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        accountEntity.setCurrency(currency);
        accountEntity.setBalance(balance);
        return accountEntity;
    }

    private TransactionEntity getTransactionEntity(String id, Currency currency, BigDecimal amount, AccountEntity sourceAccount, AccountEntity targetAccount) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(id);
        entity.setCurrency(currency);
        entity.setAmount(amount);
        entity.setSourceAccount(sourceAccount);
        entity.setTargetAccount(targetAccount);
        return entity;
    }
}