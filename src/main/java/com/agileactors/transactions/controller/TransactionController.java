package com.agileactors.transactions.controller;

import com.agileactors.transactions.entity.Currency;
import com.agileactors.transactions.exception.AccountNotFoundException;
import com.agileactors.transactions.exception.NotSufficientBalanceException;
import com.agileactors.transactions.exception.TransferBetweenSameAccountsException;
import com.agileactors.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PostMapping("")
    public ResponseEntity<HttpStatus> create(final String sourceAccountId, final String targetAccountId, final BigDecimal amount, final Optional<Currency> currency) throws TransferBetweenSameAccountsException, AccountNotFoundException, NotSufficientBalanceException {
        service.create(sourceAccountId, targetAccountId, amount, currency);
        return ResponseEntity.ok(HttpStatus.CREATED);
    }

}
