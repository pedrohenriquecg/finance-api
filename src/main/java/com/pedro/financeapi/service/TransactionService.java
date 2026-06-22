package com.pedro.financeapi.service;

import com.pedro.financeapi.dto.TransactionRequest;
import com.pedro.financeapi.exception.TransactionNotFoundException;
import com.pedro.financeapi.exception.UserNotFoundException;
import com.pedro.financeapi.model.Transaction;
import com.pedro.financeapi.model.User;
import com.pedro.financeapi.repository.TransactionRepository;
import com.pedro.financeapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Transaction salvar(TransactionRequest request) {
        Transaction transaction = new Transaction();
        preencherDados(transaction, request);
        return transactionRepository.save(transaction);
    }

    public List<Transaction> listar() {
        return transactionRepository.findAll();
    }

    public Transaction buscarPorId(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public Transaction atualizar(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        preencherDados(transaction, request);
        return transactionRepository.save(transaction);
    }

    public void deletar(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionRepository.delete(transaction);
    }

    private void preencherDados(Transaction transaction, TransactionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setDate(request.getDate());
        transaction.setUser(user);
    }
}
