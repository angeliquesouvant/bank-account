package fr.angelique.bankaccount.service.exception;

public class OverdraftAccountException extends Exception {
    public OverdraftAccountException(String message) {
        super(message);
    }
}
