package com.agileactors.transactions.repository;

import com.agileactors.transactions.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<AccountEntity, String> {
}
