package fr.angelique.bankaccount.service;

import fr.angelique.bankaccount.TextOperationsFormatter;
import fr.angelique.bankaccount.enums.OperationType;
import fr.angelique.bankaccount.service.model.Operation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextOperationsFormatterShould {

    private final TextOperationsFormatter accountStatementRowsToStringFormatter = new TextOperationsFormatter();

    @DisplayName("format a list of operations to a list of strings")
    @Test
    void formatOperationListToStringList() {
        Operation operationEntity1 = new Operation(
                OperationType.WITHDRAWAL,
                "course",
                LocalDateTime.of(2022, 11, 21, 15, 22, 48),
                new BigDecimal("10"),
                new BigDecimal("20"));
        Operation operationEntity2 = new Operation(
                OperationType.DEPOSIT,
                "cadeau",
                LocalDateTime.of(2022, 11, 21, 16, 30, 00),
                new BigDecimal("20"),
                new BigDecimal("40.30"));

        String operation1 = "Type d'opération : Retrait, " +
                        "libelle: course, " +
                        "date : 2022-11-21 15:22:48, " +
                        "montant de l'opération : 10 euros, " +
                        "solde après opération : 20 euros";

        String operation2 = "Type d'opération : Dépot, " +
                        "libelle: cadeau, " +
                        "date : 2022-11-21 16:30:00, " +
                        "montant de l'opération : 20 euros, " +
                        "solde après opération : 40,3 euros";

        // WHEN
        List<String> result = accountStatementRowsToStringFormatter.format(List.of(operationEntity1, operationEntity2));

        // THEN
        assertEquals(List.of(operation1, operation2), result);
    }

    @DisplayName("return an empty string list")
    @Test
    void returnEmptyStringList() {
        // WHEN
        List<String> result = accountStatementRowsToStringFormatter.format(List.of());

        // THEN
        assertEquals(List.of(), result);
    }
}