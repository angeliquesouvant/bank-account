package fr.angelique.bankaccount.service;

import fr.angelique.bankaccount.service.exception.AccountNotFoundException;
import fr.angelique.bankaccount.service.exception.NegativeAmountException;
import fr.angelique.bankaccount.service.exception.OverdraftAccountException;

import java.math.BigDecimal;
import java.util.UUID;

public interface BankAccountService {
    void deposit(UUID accountId, BigDecimal amount, String libelle) throws AccountNotFoundException, NegativeAmountException;
    void withdraw(UUID accountId, BigDecimal amount, String libelle) throws AccountNotFoundException, NegativeAmountException, OverdraftAccountException;
    void printAccountOperations(UUID accountId) throws AccountNotFoundException;
}
