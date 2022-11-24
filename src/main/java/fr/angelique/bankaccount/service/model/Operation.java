package fr.angelique.bankaccount.service.model;

import fr.angelique.bankaccount.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Operation {

    private final String data;
    private final OperationType operationType;
    private final LocalDateTime date;
    private final BigDecimal amount;
    private final BigDecimal balance;

    public Operation(OperationType operationType, String data, LocalDateTime date, BigDecimal amount, BigDecimal balance) {
        this.operationType = operationType;
        this.data = data;
        this.date = date;
        this.amount = amount;
        this.balance = balance;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public String getData() {
        return data;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation that = (Operation) o;
        return Objects.equals(data, that.data) && operationType == that.operationType && Objects.equals(date, that.date) && Objects.equals(amount, that.amount) && Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, operationType, date, amount, balance);
    }
}
