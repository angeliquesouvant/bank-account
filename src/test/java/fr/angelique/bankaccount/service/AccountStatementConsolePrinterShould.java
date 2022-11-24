package fr.angelique.bankaccount.service;

import fr.angelique.bankaccount.AccountStatementConsolePrinter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountStatementConsolePrinterShould {

    private ByteArrayOutputStream outContent;
    private final AccountStatementConsolePrinter accountStatementRowsPrinter = new AccountStatementConsolePrinter();
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @DisplayName("print a list of strings")
    @Test
    void printOperations() {
        // WHEN
        accountStatementRowsPrinter.print(List.of("operation1", "operation2"));

        // THEN
        assertEquals("operation1" + System.lineSeparator() + "operation2" + System.lineSeparator(), outContent.toString());
    }

    @DisplayName("print nothing")
    @Test
    void printNothing() {
        // WHEN
        accountStatementRowsPrinter.print(List.of());

        // THEN
        assertEquals("", outContent.toString());
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}