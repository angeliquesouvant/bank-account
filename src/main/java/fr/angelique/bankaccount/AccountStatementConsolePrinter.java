package fr.angelique.bankaccount;

import java.util.List;

public class AccountStatementConsolePrinter implements AccountStatementPrinter {

    @Override
    public void print(List<String> statementLines) {
        statementLines.forEach(System.out::println);
    }
}
