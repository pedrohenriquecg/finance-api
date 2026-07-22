package com.pedro.financeapi.controller;

import com.pedro.financeapi.dto.FinancialSummaryResponse;
import com.pedro.financeapi.dto.TransactionRequest;
import com.pedro.financeapi.dto.TransactionResponse;
import com.pedro.financeapi.model.User;
import com.pedro.financeapi.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService service;

    public TransactionController(TransactionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> criar(
            @AuthenticationPrincipal User authenticatedUser,
            @Valid @RequestBody TransactionRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(request, authenticatedUser));
    }

    @GetMapping
    public List<TransactionResponse> listar(@AuthenticationPrincipal User authenticatedUser) {
        return service.listar(authenticatedUser);
    }

    @GetMapping("/summary")
    public FinancialSummaryResponse resumo(@AuthenticationPrincipal User authenticatedUser) {
        return service.resumoPorUsuario(authenticatedUser);
    }

    @GetMapping("/user/{userId}")
    public List<TransactionResponse> listarPorUsuario(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long userId
    ) {
        return service.listarPorUsuario(userId, authenticatedUser);
    }

    @GetMapping("/user/{userId}/summary")
    public FinancialSummaryResponse resumoPorUsuario(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long userId
    ) {
        return service.resumoPorUsuario(userId, authenticatedUser);
    }

    @GetMapping("/{id}")
    public TransactionResponse buscarPorId(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long id
    ) {
        return service.buscarPorId(id, authenticatedUser);
    }

    @PutMapping("/{id}")
    public TransactionResponse atualizar(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request
    ) {
        return service.atualizar(id, request, authenticatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @AuthenticationPrincipal User authenticatedUser,
            @PathVariable Long id
    ) {
        service.deletar(id, authenticatedUser);
        return ResponseEntity.noContent().build();
    }
}
