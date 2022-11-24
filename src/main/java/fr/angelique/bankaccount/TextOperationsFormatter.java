package fr.angelique.bankaccount;

import fr.angelique.bankaccount.enums.OperationType;
import fr.angelique.bankaccount.service.model.Operation;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TextOperationsFormatter implements OperationsFormatter {

    @Override
    public List<String> format(List<Operation> operations) {
        DecimalFormat decimalFormat = formatDecimal();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return operations.stream()
                .map(operation -> String.format("Type d'opération : %s, " +
                                "libelle: %s, " +
                                "date : %s, " +
                                "montant de l'opération : %s euros, " +
                                "solde après opération : %s euros",
                        extractOperationLabel(operation.getOperationType()),
                        operation.getData(),
                        operation.getDate().format(dateFormat),
                        decimalFormat.format(operation.getAmount()),
                        decimalFormat.format(operation.getBalance())
                ))
                .collect(Collectors.toList());
    }
    private static DecimalFormat formatDecimal() {
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        return decimalFormat;
    }

    private String extractOperationLabel(OperationType operationType) {
        return switch (operationType) {
            case WITHDRAWAL -> "Retrait";
            case DEPOSIT -> "Dépot";
        };
    }
}
