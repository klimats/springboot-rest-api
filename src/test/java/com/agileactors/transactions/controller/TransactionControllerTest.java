package com.agileactors.transactions.controller;

import com.agileactors.transactions.App;
import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.entity.Currency;
import com.agileactors.transactions.exception.AccountNotFoundException;
import com.agileactors.transactions.exception.NotSufficientBalanceException;
import com.agileactors.transactions.exception.TransferBetweenSameAccountsException;
import com.agileactors.transactions.service.AccountService;
import com.agileactors.transactions.service.TransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.agileactors.transactions.entity.Currency.EUR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = App.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionControllerTest {

    private final String PRESAVED_ACCOUNT_ID_1 = UUID.randomUUID().toString();
    private final String PRESAVED_ACCOUNT_ID_2 = UUID.randomUUID().toString();

    @Autowired
    private MockMvc mvc;

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
    @Test
    void ac1() throws Exception {

        mvc.perform(
                post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sourceAccountId", PRESAVED_ACCOUNT_ID_1)
                        .param("targetAccountId", PRESAVED_ACCOUNT_ID_2)
                        .param("amount", BigDecimal.valueOf(950d).toString()))
                .andDo(print())
                .andExpect(status().isOk());

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
    @Test
    void ac2() throws Exception {

        mvc.perform(
                post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sourceAccountId", PRESAVED_ACCOUNT_ID_1)
                        .param("targetAccountId", PRESAVED_ACCOUNT_ID_2)
                        .param("amount", BigDecimal.valueOf(10050d).toString()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof NotSufficientBalanceException))
                .andExpect(result -> assertEquals("The balance of the source account is less from the transaction amount.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    /**
     * Transfer between same account
     */
    @Test
    void ac3() throws Exception {

        mvc.perform(
                post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sourceAccountId", PRESAVED_ACCOUNT_ID_1)
                        .param("targetAccountId", PRESAVED_ACCOUNT_ID_1)
                        .param("amount", BigDecimal.valueOf(100d).toString()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof TransferBetweenSameAccountsException))
                .andExpect(result -> assertEquals("This operation is not allowed on the same accounts.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    /**
     * One or more of the accounts does not exist
     */
    @Test
    void ac4() throws Exception {

        mvc.perform(
                post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("sourceAccountId", PRESAVED_ACCOUNT_ID_1)
                        .param("targetAccountId", "WRONG_ACCOUNT")
                        .param("amount", BigDecimal.valueOf(10050d).toString()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AccountNotFoundException))
                .andExpect(result -> assertEquals("Target account does not exist.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    private AccountEntity getAccountEntity(final String id, final Currency currency, final BigDecimal balance) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        accountEntity.setCurrency(currency);
        accountEntity.setBalance(balance);
        return accountEntity;
    }
}