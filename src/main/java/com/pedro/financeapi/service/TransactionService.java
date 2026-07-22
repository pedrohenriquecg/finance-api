package com.pedro.financeapi.service;

import com.pedro.financeapi.dto.FinancialSummaryResponse;
import com.pedro.financeapi.dto.TransactionRequest;
import com.pedro.financeapi.dto.TransactionResponse;
import com.pedro.financeapi.exception.TransactionNotFoundException;
import com.pedro.financeapi.model.Transaction;
import com.pedro.financeapi.model.TransactionType;
import com.pedro.financeapi.model.User;
import com.pedro.financeapi.repository.TransactionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse salvar(TransactionRequest request, User authenticatedUser) {
        Transaction transaction = new Transaction();
        preencherDados(transaction, request, authenticatedUser);
        return new TransactionResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> listar(User authenticatedUser) {
        return transactionRepository.findByUserIdOrderByDateDescIdDesc(authenticatedUser.getId())
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    public List<TransactionResponse> listarPorUsuario(Long userId, User authenticatedUser) {
        validarDono(userId, authenticatedUser);

        return listar(authenticatedUser);
    }

    public TransactionResponse buscarPorId(Long id, User authenticatedUser) {
        return new TransactionResponse(buscarEntidadePorId(id, authenticatedUser));
    }

    public TransactionResponse atualizar(Long id, TransactionRequest request, User authenticatedUser) {
        Transaction transaction = buscarEntidadePorId(id, authenticatedUser);

        preencherDados(transaction, request, authenticatedUser);
        return new TransactionResponse(transactionRepository.save(transaction));
    }

    public FinancialSummaryResponse resumoPorUsuario(User authenticatedUser) {
        List<Transaction> transactions = transactionRepository.findByUserId(authenticatedUser.getId());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            }

            if (transaction.getType() == TransactionType.EXPENSE) {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        return new FinancialSummaryResponse(
                authenticatedUser.getId(),
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense),
                transactions.size()
        );
    }

    public FinancialSummaryResponse resumoPorUsuario(Long userId, User authenticatedUser) {
        validarDono(userId, authenticatedUser);

        return resumoPorUsuario(authenticatedUser);
    }

    public void deletar(Long id, User authenticatedUser) {
        Transaction transaction = buscarEntidadePorId(id, authenticatedUser);

        transactionRepository.delete(transaction);
    }

    private void preencherDados(Transaction transaction, TransactionRequest request, User authenticatedUser) {
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setUser(authenticatedUser);
    }

    private Transaction buscarEntidadePorId(Long id, User authenticatedUser) {
        return transactionRepository.findByIdAndUserId(id, authenticatedUser.getId())
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    private void validarDono(Long userId, User authenticatedUser) {
        if (!authenticatedUser.getId().equals(userId)) {
            throw new AccessDeniedException("You can only access your own financial data");
        }
    }
}
