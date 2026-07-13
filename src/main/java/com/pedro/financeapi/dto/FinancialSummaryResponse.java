package com.pedro.financeapi.dto;

import java.math.BigDecimal;

public class FinancialSummaryResponse {

    private final Long userId;
    private final BigDecimal totalIncome;
    private final BigDecimal totalExpense;
    private final BigDecimal balance;
    private final int transactionCount;

    public FinancialSummaryResponse(
            Long userId,
            BigDecimal totalIncome,
            BigDecimal totalExpense,
            BigDecimal balance,
            int transactionCount
    ) {
        this.userId = userId;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.transactionCount = transactionCount;
    }

    public Long getUserId() {
        return userId;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public int getTransactionCount() {
        return transactionCount;
    }
}
