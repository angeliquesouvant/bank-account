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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountCheckingServiceShould {
    private final static LocalDateTime LOCAL_DATE = LocalDateTime.of(
            2022, 11, 21, 15, 22, 48, 123456789);

    private final ZoneId zone = ZoneId.systemDefault();
    private final Clock fixedClock = Clock.fixed(LOCAL_DATE.atZone(zone).toInstant(), zone);

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private OperationsFormatter operationsFormatter;

    @Mock
    private AccountStatementPrinter accountStatementPrinter;

    private BankAccountCheckingService bankAccountCheckingService;

    @BeforeEach
    void setUp() {
        bankAccountCheckingService = new BankAccountCheckingService(accountRepository, operationRepository, operationsFormatter, accountStatementPrinter, fixedClock);
    }

    @Test
    @DisplayName("add the deposit amount on the account and save the operation")
    void deposit() throws NegativeAmountException, AccountNotFoundException {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        String libelle = "cadeau pour noel";
        BigDecimal depositAmount = new BigDecimal("20");
        BigDecimal finalAmountOnAccount = new BigDecimal("50");
        InOrder inOrder = inOrder(accountRepository, operationRepository);

        when(accountRepository.existsById(uuid)).thenReturn(true);
        when(accountRepository.getBalance(uuid)).thenReturn(new BigDecimal("30"));

        // WHEN
        bankAccountCheckingService.deposit(uuid, depositAmount, libelle);

        // THEN
        inOrder.verify(accountRepository).existsById(uuid);
        inOrder.verify(accountRepository).getBalance(uuid);
        inOrder.verify(operationRepository).create(new Operation(OperationType.DEPOSIT, libelle, LOCAL_DATE, depositAmount, finalAmountOnAccount));
        inOrder.verify(accountRepository).updateBalance(uuid, finalAmountOnAccount);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    @DisplayName("throw NegativeAmountException when the deposit amount is negative")
    void depositNegativeAmountException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();

        // WHEN
        assertThrows(NegativeAmountException.class, () -> bankAccountCheckingService.deposit(uuid, new BigDecimal("-1000.5"), "test"));

        // THEN
        verifyNoInteractions(operationRepository, operationsFormatter, accountStatementPrinter);
    }

    @Test
    @DisplayName("throw UnknownAccountException for a deposit when the account is not found by id")
    void depositUnknownAccountException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        when(accountRepository.existsById(uuid)).thenReturn(false);

        // WHEN
        assertThrows(AccountNotFoundException.class, () ->
                bankAccountCheckingService.deposit(uuid, new BigDecimal("1000"), "test"));

        // THEN
        verify(accountRepository).existsById(uuid);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(operationRepository, operationsFormatter, accountStatementPrinter);
    }

    @Test
    @DisplayName("subtract the withdraw amount on the account and save the operation")
    void withdraw() throws NegativeAmountException, AccountNotFoundException, OverdraftAccountException {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        InOrder inOrder = inOrder(accountRepository, operationRepository);
        BigDecimal withdrawAmount = new BigDecimal("30");
        String libelle = "course";
        BigDecimal finalAmountOnAccount = new BigDecimal("970");

        when(accountRepository.existsById(uuid)).thenReturn(true);
        when(accountRepository.getBalance(uuid)).thenReturn(new BigDecimal("1000"));

        // WHEN
        bankAccountCheckingService.withdraw(uuid, withdrawAmount, libelle);

        // THEN
        inOrder.verify(accountRepository).existsById(uuid);
        inOrder.verify(accountRepository).getBalance(uuid);
        inOrder.verify(operationRepository).create(new Operation(OperationType.WITHDRAWAL, libelle, LOCAL_DATE, withdrawAmount, finalAmountOnAccount));
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
                bankAccountCheckingService.withdraw(uuid, new BigDecimal("-1000.5"), "test"));

        // THEN
        verifyNoInteractions(accountRepository, operationRepository);
    }

    @Test
    @DisplayName("throw UnknownAccountException for a withdraw when the account is not found by id")
    void withdrawAccountNotFoundException() {
        // GIVEN
        UUID uuid = UUID.randomUUID();
        when(accountRepository.existsById(uuid)).thenReturn(false);

        // WHEN
        assertThrows(AccountNotFoundException.class, () ->
                bankAccountCheckingService.withdraw(uuid, new BigDecimal("1000"), "test"));

        // THEN
        verify(accountRepository).existsById(uuid);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(operationRepository, operationsFormatter, accountStatementPrinter);
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
                bankAccountCheckingService.withdraw(uuid, new BigDecimal("1000"), "test"));

        // THEN
        verify(accountRepository).existsById(uuid);
        verify(accountRepository).getBalance(uuid);
        verifyNoMoreInteractions(accountRepository);
        verifyNoInteractions(operationRepository, operationsFormatter, accountStatementPrinter);
    }

    @Test
    @DisplayName("format a list of operations as a list of strings and print it")
    void printAllOperations() {

        // GIVEN
        UUID id = UUID.randomUUID();

        Operation operationEntity = new Operation(OperationType.WITHDRAWAL, "Retrait", LOCAL_DATE, new BigDecimal("10"), new BigDecimal("10"));
        Operation operationEntity2 = new Operation(OperationType.DEPOSIT, "Dépot", LOCAL_DATE, new BigDecimal("20"), new BigDecimal("30"));
        Operation operationEntity3 = new Operation(OperationType.WITHDRAWAL, "Retrait", LOCAL_DATE, new BigDecimal("40"), new BigDecimal("300"));
        List<Operation> operationEntities = List.of(operationEntity, operationEntity2, operationEntity3);

        String operation1 = "Type d'opération: Retrait, libelle: Retrait2, date: 2022-11-21T15:22, montant : 10, solde après opération: 10";
        String operation2 = "Type d'opération: Dépot, libelle: Dépot1, date: 2022-11-21T16:30, montant : 10, solde après opération: 10";
        String operation3 = "Type d'opération: Retrait, libelle: Retrait1, date: 2022-11-21T17, montant : 10, solde après opération: 10";
        List<String> operationStrings = List.of(operation1, operation2, operation3);

        InOrder inOrder = inOrder(accountRepository, operationRepository, operationsFormatter, accountStatementPrinter);

        when(operationRepository.findAllByAccountIdOrderByDateDesc(id)).thenReturn(operationEntities);
        when(operationsFormatter.format(operationEntities)).thenReturn(operationStrings);

        // WHEN
        bankAccountCheckingService.printAccountOperations(id);

        // THEN
        inOrder.verify(operationRepository).findAllByAccountIdOrderByDateDesc(id);
        inOrder.verify(operationsFormatter).format(operationEntities);
        inOrder.verify(accountStatementPrinter).print(operationStrings);
        inOrder.verifyNoMoreInteractions();
    }
}