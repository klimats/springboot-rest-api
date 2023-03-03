package com.agileactors.transactions.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity(name="transaction")
public class TransactionEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @ManyToOne
    @JoinColumn(
            name = "sourceAccountId",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_transaction_account_source_account_id")
    )
    private AccountEntity sourceAccount;

    @ManyToOne
    @JoinColumn(
            name = "targetAccountId",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_transaction_account_target_account_id")
    )
    private AccountEntity targetAccount;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name="currency", nullable=false)
    private Currency currency = Currency.EUR;

}
