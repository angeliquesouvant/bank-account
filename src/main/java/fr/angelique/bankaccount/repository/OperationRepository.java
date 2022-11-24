package fr.angelique.bankaccount.repository;

import fr.angelique.bankaccount.service.model.Operation;

import java.util.List;
import java.util.UUID;

public interface OperationRepository {
    List<Operation> findAllByAccountIdOrderByDateDesc(UUID id);

    void create(Operation entity);
}
