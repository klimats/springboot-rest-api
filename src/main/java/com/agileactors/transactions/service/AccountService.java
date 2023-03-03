package com.agileactors.transactions.service;

import com.agileactors.transactions.entity.AccountEntity;
import com.agileactors.transactions.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Optional<AccountEntity> findAccountById(final String id) {
        return accountRepository.findById(id);
    }

    public Iterable<AccountEntity> findAllAccounts() {
        return accountRepository.findAll();
    }
}
