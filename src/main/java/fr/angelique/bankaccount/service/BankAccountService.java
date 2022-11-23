package fr.angelique.bankaccount.service;

import fr.angelique.bankaccount.service.exception.AccountNotFoundException;
import fr.angelique.bankaccount.service.exception.NegativeAmountException;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankAccountService {
    void deposit(UUID accountId, BigDecimal amount) throws AccountNotFoundException, NegativeAmountException;
}
