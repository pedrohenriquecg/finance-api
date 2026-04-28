package com.pedro.financeapi.service;

import com.pedro.financeapi.model.User;
import com.pedro.financeapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public User salvar(User user) {
        return repository.save(user);
    }

    public List<User> listar() {
        return repository.findAll();
    }

    public User buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    public User atualizar(Long id, User userAtualizado) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        user.setName(userAtualizado.getName());
        user.setEmail(userAtualizado.getEmail());

        return repository.save(user);
    }

    public void deletar(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        repository.delete(user);
    }
}