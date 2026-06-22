package com.pedro.financeapi.controller;

import com.pedro.financeapi.model.User;
import com.pedro.financeapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> criar(@Valid @RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(user));
    }

    @GetMapping
    public List<User> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public User buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public User atualizar(@PathVariable Long id, @Valid @RequestBody User user) {
        return service.atualizar(id, user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
