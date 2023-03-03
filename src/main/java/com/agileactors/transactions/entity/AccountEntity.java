package com.agileactors.transactions.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity( name="account" )
public class AccountEntity {

    @Id
    @Column(name="id", nullable=false)
    private String id;

    @Min(value = 0)
    @Column(name="balance", nullable=false )
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name="currency", nullable=false)
    private Currency currency;

    @Column(name="createdAt", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    private void touch() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
