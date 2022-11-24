package fr.angelique.bankaccount.service;

import fr.angelique.bankaccount.AccountStatementPrinter;
import fr.angelique.bankaccount.OperationsFormatter;
import fr.angelique.bankaccount.enums.OperationType;
import fr.angelique.bankaccount.repository.AccountRepository;
import fr.angelique.bankaccount.repository.OperationRepository;
import fr.angelique.bankaccount.service.exception.AccountNotFoundException;
import fr.angelique.bankaccount.service.exception.NegativeAmountException;
import fr.angelique.bankaccount.service.exception.OverdraftAccountException;
import fr.angelique.bankaccount.service.model.Operation;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class BankAccountCheckingService implements BankAccountService {

    private final AccountRepository accountRepository;

    private final OperationRepository operationRepository;

    private final OperationsFormatter operationListFormatter;

    private final AccountStatementPrinter accountStatementPrinter;

    private final Clock clock;

    public BankAccountCheckingService(AccountRepository accountRepository, OperationRepository operationRepository, OperationsFormatter operationListFormatter, AccountStatementPrinter accountStatementPrinter, Clock clock) {
        this.accountRepository = accountRepository;
        this.operationRepository = operationRepository;
        this.operationListFormatter = operationListFormatter;
        this.accountStatementPrinter = accountStatementPrinter;
        this.clock = clock;
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount, String libelle) throws NegativeAmountException, AccountNotFoundException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Les valeurs négatives ne sont pas acceptées");
        }
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Le compte n'existe pas");
        }
        BigDecimal newAmount = accountRepository.getBalance(accountId).add(amount);
        operationRepository.create(new Operation(OperationType.DEPOSIT, libelle, LocalDateTime.now(clock), amount, newAmount));
        accountRepository.updateBalance(accountId, newAmount);
    }

    @Override
    public void withdraw(UUID accountId, BigDecimal amount, String libelle) throws NegativeAmountException, AccountNotFoundException, OverdraftAccountException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Les valeurs négatives ne sont pas acceptées");
        }
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Le compte n'existe pas");
        }
        BigDecimal newAmount = accountRepository.getBalance(accountId).subtract(amount);
        if (newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new OverdraftAccountException("Le montant de retrait est supérieur au solde sur le compte");
        }
        operationRepository.create(new Operation(OperationType.WITHDRAWAL, libelle, LocalDateTime.now(clock), amount, newAmount));
        accountRepository.updateBalance(accountId, newAmount);
    }

    @Override
    public void printAccountOperations(UUID accountId) {
        List<Operation> operations = operationRepository.findAllByAccountIdOrderByDateDesc(accountId);
        List<String> lines = operationListFormatter.format(operations);
        accountStatementPrinter.print(lines);
    }
}
