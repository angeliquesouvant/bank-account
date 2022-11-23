package fr.angelique.bankaccount.service;

import fr.angelique.bankaccount.repository.AccountRepository;
import fr.angelique.bankaccount.service.exception.AccountNotFoundException;
import fr.angelique.bankaccount.service.exception.NegativeAmountException;
import java.math.BigDecimal;
import java.util.UUID;

public class BankAccountCheckingService implements BankAccountService {

    private final AccountRepository accountRepository;

    public BankAccountCheckingService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void deposit(UUID accountId, BigDecimal amount) throws NegativeAmountException, AccountNotFoundException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeAmountException("Les valeurs négatives ne sont pas acceptées");
        }
        if (!accountRepository.existsById(accountId)) {
            throw new AccountNotFoundException("Le compte n'existe pas");
        }
        BigDecimal newAmount = accountRepository.getBalance(accountId).add(amount);
        accountRepository.updateBalance(accountId, newAmount);
    }
}
