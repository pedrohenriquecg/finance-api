package com.pedro.financeapi.service;

import com.pedro.financeapi.dto.UserRequest;
import com.pedro.financeapi.dto.UserResponse;
import com.pedro.financeapi.exception.ResourceConflictException;
import com.pedro.financeapi.exception.UserNotFoundException;
import com.pedro.financeapi.model.User;
import com.pedro.financeapi.repository.TransactionRepository;
import com.pedro.financeapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public UserService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public UserResponse buscarPerfil(User authenticatedUser) {
        return new UserResponse(buscarEntidadePorId(authenticatedUser.getId()));
    }

    public UserResponse atualizarPerfil(User authenticatedUser, UserRequest request) {
        User user = buscarEntidadePorId(authenticatedUser.getId());

        preencherDados(user, request);
        return new UserResponse(userRepository.save(user));
    }

    private User buscarEntidadePorId(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void deletarPerfil(User authenticatedUser) {
        User user = buscarEntidadePorId(authenticatedUser.getId());

        if (transactionRepository.existsByUserId(user.getId())) {
            throw new ResourceConflictException("User has transactions and cannot be deleted");
        }

        userRepository.delete(user);
    }

    private void preencherDados(User user, UserRequest request) {
        String email = normalizeEmail(request.getEmail());

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new ResourceConflictException("Email is already in use");
        }

        user.setName(request.getName());
        user.setEmail(email);
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
