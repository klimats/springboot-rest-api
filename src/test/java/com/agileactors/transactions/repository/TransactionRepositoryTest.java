package com.agileactors.transactions.repository;

import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.entity.Currency;
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
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionRepositoryTest {

    private final String PRESAVED_ACCOUNT_ID_1 = UUID.randomUUID().toString();
    private final String PRESAVED_ACCOUNT_ID_2 = UUID.randomUUID().toString();

    @Autowired
    private AccountRepository repository;

    @BeforeAll
    void setUp() {
        AccountEntity presavedEntity1 = getEntity(PRESAVED_ACCOUNT_ID_1, GBP, BigDecimal.valueOf(100d));
        AccountEntity presavedEntity2 = getEntity(PRESAVED_ACCOUNT_ID_2, USD, BigDecimal.valueOf(100d));

        repository.saveAll(Arrays.asList(presavedEntity1, presavedEntity2));
    }

    @Test
    void itShouldCreateAccount() {
        String id = UUID.randomUUID().toString();
        AccountEntity savedEntity = repository.save(getEntity(id, Currency.EUR, BigDecimal.TEN));

        assertNotNull(savedEntity);
        assertNotNull(savedEntity.getId());
        assertEquals(savedEntity.getId(), id);
        assertNotNull(savedEntity.getCreatedAt());
    }

    @Test
    void itShouldFindAccount() {
        Optional<AccountEntity> savedEntityOpt = repository.findById(PRESAVED_ACCOUNT_ID_1);
        assertTrue(savedEntityOpt.isPresent());

        AccountEntity accountEntity = savedEntityOpt.get();
        assertNotNull(accountEntity);
        assertNotNull(accountEntity.getId());
        assertEquals(accountEntity.getId(), PRESAVED_ACCOUNT_ID_1);
        assertEquals(GBP, accountEntity.getCurrency());
    }

    private AccountEntity getEntity(final String id, final Currency currency, final BigDecimal balance) {
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setId(id);
        accountEntity.setCurrency(currency);
        accountEntity.setBalance(balance);
        return accountEntity;
    }
}