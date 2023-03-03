package com.agileactors.transactions.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Currency {
    EUR("EUR"), USD("USD"), GBP("GBP");

    private final String value;
}
