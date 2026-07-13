package com.pedro.financeapi.service;

import com.pedro.financeapi.dto.UserRequest;
import com.pedro.financeapi.dto.UserResponse;
import com.pedro.financeapi.exception.UserNotFoundException;
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

    public UserResponse salvar(UserRequest request) {
        User user = new User();
        preencherDados(user, request);
        return new UserResponse(repository.save(user));
    }

    public List<UserResponse> listar() {
        return repository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    public UserResponse buscarPorId(Long id) {
        return new UserResponse(buscarEntidadePorId(id));
    }

    public UserResponse atualizar(Long id, UserRequest request) {
        User user = buscarEntidadePorId(id);

        preencherDados(user, request);
        return new UserResponse(repository.save(user));
    }

    private User buscarEntidadePorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void deletar(Long id) {
        User user = buscarEntidadePorId(id);

        repository.delete(user);
    }

    private void preencherDados(User user, UserRequest request) {
        user.setName(request.getName());
        user.setEmail(request.getEmail());
    }
}
