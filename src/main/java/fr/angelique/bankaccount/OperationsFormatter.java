package fr.angelique.bankaccount;

import fr.angelique.bankaccount.service.model.Operation;

import java.util.List;

public interface OperationsFormatter {
    List<String> format(List<Operation> operations);
}
