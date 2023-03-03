package com.agileactors.transactions.service;

import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.entity.Currency;
import com.agileactors.transactions.entity.TransactionEntity;
import com.agileactors.transactions.exception.AccountNotFoundException;
import com.agileactors.transactions.exception.NotSufficientBalanceException;
import com.agileactors.transactions.exception.TransferBetweenSameAccountsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;
import java.util.Optional;

import static com.agileactors.transactions.entity.Currency.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionServiceTest {

    private final String PRESAVED_ACCOUNT_ID_1 = UUID.randomUUID().toString();
    private final String PRESAVED_ACCOUNT_ID_2 = UUID.randomUUID().toString();

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @BeforeAll
    void setUp() {
        AccountEntity presavedEntity1 = getAccountEntity(PRESAVED_ACCOUNT_ID_1, EUR, BigDecimal.valueOf(1000d));
        AccountEntity presavedEntity2 = getAccountEntity(PRESAVED_ACCOUNT_ID_2, EUR, BigDecimal.valueOf(0d));
        transactionService.saveAllTransactions(Arrays.asList(presavedEntity1, presavedEntity2));
    }


    /**
     * Happy path for money transfer between two accounts
     */
    @Test()
    void itShouldMakeTheTransfer() throws TransferBetweenSameAccountsException, AccountNotFoundException, NotSufficientBalanceException {
        TransactionEntity savedEntity = transactionService.create(PRESAVED_ACCOUNT_ID_1, PRESAVED_ACCOUNT_ID_2, BigDecimal.valueOf(950d), Optional.empty());
        assertNotNull(savedEntity);

        Optional<AccountEntity> sourceAccountOpt = accountService.findAccountById(PRESAVED_ACCOUNT_ID_1);
        assertTrue(sourceAccountOpt.isPresent());
        Optional<AccountEntity> targetAccountOpt = accountService.findAccountById(PRESAVED_ACCOUNT_ID_2);
        assertTrue(targetAccountOpt.isPresent());

        assertEquals(0, sourceAccountOpt.get().getBalance().compareTo(BigDecimal.valueOf(50d)));
        assertEquals(0, targetAccountOpt.get().getBalance().compareTo(BigDecimal.valueOf(950d)));
    }

    /**
     * Insufficient balance to process money transfer
     */
    @Test()
    void itShouldBlockTheTransferForInsufficientBalance() throws TransferBetweenSameAccountsException, AccountNotFoundException, NotSufficientBalanceException {
        NotSufficientBalanceException thrown = assertThrows(NotSufficientBalanceException.class, () -> {
            transactionService.create(PRESAVED_ACCOUNT_ID_1, PRESAVED_ACCOUNT_ID_2, BigDecimal.valueOf(10050d), Optional.empty());
        });

        Assertions.assertEquals("The balance of the source account is less from the transaction amount.", thrown.getMessage());
    }

    /**
     * Transfer between same account
     */
    @Test()
    void itShouldBlockTheTransferForTransferBetweenTheSameAccounts() throws TransferBetweenSameAccountsException, AccountNotFoundException, NotSufficientBalanceException {
        TransferBetweenSameAccountsException thrown = assertThrows(TransferBetweenSameAccountsException.class, () -> {
            transactionService.create(PRESAVED_ACCOUNT_ID_1, PRESAVED_ACCOUNT_ID_1, BigDecimal.valueOf(100d), Optional.empty());
        });

        Assertions.assertEquals("This operation is not allowed on the same accounts.", thrown.getMessage());
    }

    /**
     * One or more of the accounts does not exist
     */
    @Test()
    void itShouldBlockTheTransferForInexistingSourceAccount() throws TransferBetweenSameAccountsException, AccountNotFoundException, NotSufficientBalanceException {
        AccountNotFoundException thrown = assertThrows(AccountNotFoundException.class, () -> {
            transactionService.create("WRONG_ACCOUNT", PRESAVED_ACCOUNT_ID_2, BigDecimal.valueOf(100d), Optional.empty());
        });

        Assertions.assertEquals("Source account does not exist.", thrown.getMessage());
    }

    /**
     * One or more of the accounts does not exist
     */
    @Test()
    void itShouldBlockTheTransferForInexistingTargetAccount() throws TransferBetweenSameAccountsException, AccountNotFoundException, NotSufficientBalanceException {
        AccountNotFoundException thrown = assertThrows(AccountNotFoundException.class, () -> {
            transactionService.create(PRESAVED_ACCOUNT_ID_1, "WRONG_ACCOUNT", BigDecimal.valueOf(100d), Optional.empty());
        });

        Assertions.assertEquals("Target account does not exist.", thrown.getMessage());
    }

    private AccountEntity getAccountEntity(final String id, final Currency currency, final BigDecimal balance) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        accountEntity.setCurrency(currency);
        accountEntity.setBalance(balance);
        return accountEntity;
    }
}