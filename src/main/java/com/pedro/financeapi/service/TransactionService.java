package com.pedro.financeapi.service;

import com.pedro.financeapi.dto.FinancialSummaryResponse;
import com.pedro.financeapi.dto.TransactionRequest;
import com.pedro.financeapi.dto.TransactionResponse;
import com.pedro.financeapi.exception.TransactionNotFoundException;
import com.pedro.financeapi.exception.UserNotFoundException;
import com.pedro.financeapi.model.Transaction;
import com.pedro.financeapi.model.TransactionType;
import com.pedro.financeapi.model.User;
import com.pedro.financeapi.repository.TransactionRepository;
import com.pedro.financeapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public TransactionResponse salvar(TransactionRequest request) {
        Transaction transaction = new Transaction();
        preencherDados(transaction, request);
        return new TransactionResponse(transactionRepository.save(transaction));
    }

    public List<TransactionResponse> listar() {
        return transactionRepository.findAll()
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    public List<TransactionResponse> listarPorUsuario(Long userId) {
        buscarUsuarioPorId(userId);

        return transactionRepository.findByUserId(userId)
                .stream()
                .map(TransactionResponse::new)
                .toList();
    }

    public TransactionResponse buscarPorId(Long id) {
        return new TransactionResponse(buscarEntidadePorId(id));
    }

    public TransactionResponse atualizar(Long id, TransactionRequest request) {
        Transaction transaction = buscarEntidadePorId(id);

        preencherDados(transaction, request);
        return new TransactionResponse(transactionRepository.save(transaction));
    }

    public FinancialSummaryResponse resumoPorUsuario(Long userId) {
        User user = buscarUsuarioPorId(userId);
        List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

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
                user.getId(),
                totalIncome,
                totalExpense,
                totalIncome.subtract(totalExpense),
                transactions.size()
        );
    }

    public void deletar(Long id) {
        Transaction transaction = buscarEntidadePorId(id);

        transactionRepository.delete(transaction);
    }

    private void preencherDados(Transaction transaction, TransactionRequest request) {
        User user = buscarUsuarioPorId(request.getUserId());

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setUser(user);
    }

    private Transaction buscarEntidadePorId(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    private User buscarUsuarioPorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
