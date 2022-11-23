package fr.angelique.bankaccount.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountRepository {
    boolean existsById(UUID id);

    BigDecimal getBalance(UUID id);

    void updateBalance(UUID id, BigDecimal amount);
}
