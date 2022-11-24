package fr.angelique.bankaccount;

import java.util.List;

public interface AccountStatementPrinter {
    void print(List<String> statementLines);
}
