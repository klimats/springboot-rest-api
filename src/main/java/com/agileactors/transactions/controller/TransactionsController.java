package com.agileactors.transactions.controller;

import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.entity.TransactionEntity;
import com.agileactors.transactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionsController {

    private final TransactionService service;

    @GetMapping("")
    public ResponseEntity<Iterable<TransactionEntity>> getTransactionsList() {
        return ResponseEntity.ok(service.findAllTransactions());
    }
}
