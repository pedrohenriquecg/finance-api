package com.pedro.financeapi.controller;

import com.pedro.financeapi.dto.UserRequest;
import com.pedro.financeapi.dto.UserResponse;
import com.pedro.financeapi.model.User;
import com.pedro.financeapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UserResponse buscarPerfil(@AuthenticationPrincipal User authenticatedUser) {
        return service.buscarPerfil(authenticatedUser);
    }

    @PutMapping("/me")
    public UserResponse atualizarPerfil(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody UserRequest request
    ) {
        return service.atualizarPerfil(authenticatedUser, request);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deletarPerfil(@AuthenticationPrincipal User authenticatedUser) {
        service.deletarPerfil(authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
