package com.agileactors.transactions.controller;

import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountsController {

    private final AccountService service;

    @GetMapping("")
    public ResponseEntity<Iterable<AccountEntity>> getAccountsList() {
        return ResponseEntity.ok(service.findAllAccounts());
    }
}
