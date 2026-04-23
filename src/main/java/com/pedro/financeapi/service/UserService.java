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
}