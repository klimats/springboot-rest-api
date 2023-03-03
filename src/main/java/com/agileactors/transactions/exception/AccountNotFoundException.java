package com.agileactors.transactions.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class AccountNotFoundException extends Exception {

    private final String message;

    public AccountNotFoundException(final String message) {
        super(message);
        this.message = message;
    }
}
