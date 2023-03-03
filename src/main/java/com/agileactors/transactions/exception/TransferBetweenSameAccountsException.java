package com.agileactors.transactions.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TransferBetweenSameAccountsException extends Exception {

    private final String message;

    public TransferBetweenSameAccountsException(final String message) {
        super(message);
        this.message = message;
    }

}
