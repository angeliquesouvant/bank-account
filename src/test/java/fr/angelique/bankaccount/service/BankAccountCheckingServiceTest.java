package fr.angelique.bankaccount.service;

import fr.angelique.bankaccount.repository.AccountRepository;
import fr.angelique.bankaccount.service.exception.AccountNotFoundException;
import fr.angelique.bankaccount.service.exception.NegativeAmountException;
import fr.angelique.bankaccount.service.exception.OverdraftAccountException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountCheckingServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BankAccountCheckingService bankAccountCheckingService;

    @Test
    @DisplayName("sum the deposit amount and update the account balance")
    void deposit() throws NegativeAmountException, AccountNotFoundException {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        BigDecimal depositAmount = new BigDecimal("20");
        BigDecimal finalAmountOnAccount = new BigDecimal("50");
        InOrder inOrder = inOrder(accountRepository);

        when(accountRepository.existsById(uuid)).thenReturn(true);
        when(accountRepository.getBalance(uuid)).thenReturn(new BigDecimal("30"));

        // WHEN
        bankAccountCheckingService.deposit(uuid, depositAmount);

        // THEN
        inOrder.verify(accountRepository).existsById(uuid);
        inOrder.verify(accountRepository).getBalance(uuid);
        inOrder.verify(accountRepository).updateBalance(uuid, finalAmountOnAccount);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("throw NegativeAmountException when the deposit amount is negative")
    void depositNegativeAmountException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();

        // WHEN
        assertThrows(NegativeAmountException.class, () -> bankAccountCheckingService.deposit(uuid, new BigDecimal("-1000.5")));

        // THEN
        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("throw UnknownAccountException for a deposit when the account is not found by id")
    void depositUnknownAccountException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        when(accountRepository.existsById(uuid)).thenReturn(false);

        // WHEN
        assertThrows(AccountNotFoundException.class, () ->
                bankAccountCheckingService.deposit(uuid, new BigDecimal("1000")));

        // THEN
        verify(accountRepository).existsById(uuid);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    @DisplayName("subtract the withdraw amount and update the account balance")
    void withdraw() throws NegativeAmountException, AccountNotFoundException, OverdraftAccountException {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        InOrder inOrder = inOrder(accountRepository);
        BigDecimal withdrawAmount = new BigDecimal("30");
        BigDecimal finalAmountOnAccount = new BigDecimal("970");

        when(accountRepository.existsById(uuid)).thenReturn(true);
        when(accountRepository.getBalance(uuid)).thenReturn(new BigDecimal("1000"));

        // WHEN
        bankAccountCheckingService.withdraw(uuid, withdrawAmount);

        // THEN
        inOrder.verify(accountRepository).existsById(uuid);
        inOrder.verify(accountRepository).getBalance(uuid);
        inOrder.verify(accountRepository).updateBalance(uuid, finalAmountOnAccount);
        inOrder.verifyNoMoreInteractions();
    }


    @Test
    @DisplayName("throw NegativeAmountException when the withdraw value is negative")
    void withdrawNegativeAmountException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();

        // WHEN
        assertThrows(NegativeAmountException.class, () ->
                bankAccountCheckingService.withdraw(uuid, new BigDecimal("-1000.5")));

        // THEN
        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("throw UnknownAccountException for a withdraw when the account is not found by id")
    void withdrawAccountNotFoundException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        when(accountRepository.existsById(uuid)).thenReturn(false);

        // WHEN
        assertThrows(AccountNotFoundException.class, () ->
                bankAccountCheckingService.withdraw(uuid, new BigDecimal("1000")));

        // THEN
        verify(accountRepository).existsById(uuid);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    @DisplayName("throw OverdraftAccountException when the withdraw amount is greater than account balance")
    void withdrawOverdraftAccountException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        when(accountRepository.existsById(uuid)).thenReturn(true);
        when(accountRepository.getBalance(uuid)).thenReturn(new BigDecimal("500"));

        // WHEN
        assertThrows(OverdraftAccountException.class, () ->
                bankAccountCheckingService.withdraw(uuid, new BigDecimal("1000")));

        // THEN
        verify(accountRepository).existsById(uuid);
        verify(accountRepository).getBalance(uuid);
        verifyNoMoreInteractions(accountRepository);
    }
}