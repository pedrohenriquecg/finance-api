package com.pedro.financeapi.dto;

import com.pedro.financeapi.model.Transaction;
import com.pedro.financeapi.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {

    private final Long id;
    private final String description;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String category;
    private final LocalDate date;
    private final UserResponse user;

    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.description = transaction.getDescription();
        this.amount = transaction.getAmount();
        this.type = transaction.getType();
        this.category = transaction.getCategory();
        this.date = transaction.getDate();
        this.user = new UserResponse(transaction.getUser());
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public UserResponse getUser() {
        return user;
    }
}
