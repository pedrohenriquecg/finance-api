package com.pedro.financeapi.controller;

import com.pedro.financeapi.dto.FinancialSummaryResponse;
import com.pedro.financeapi.dto.TransactionRequest;
import com.pedro.financeapi.dto.TransactionResponse;
import com.pedro.financeapi.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TransactionResponse> criar(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.salvar(request));
    }

    @GetMapping
    public List<TransactionResponse> listar() {
        return service.listar();
    }

    @GetMapping("/user/{userId}")
    public List<TransactionResponse> listarPorUsuario(@PathVariable Long userId) {
        return service.listarPorUsuario(userId);
    }

    @GetMapping("/user/{userId}/summary")
    public FinancialSummaryResponse resumoPorUsuario(@PathVariable Long userId) {
        return service.resumoPorUsuario(userId);
    }

    @GetMapping("/{id}")
    public TransactionResponse buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public TransactionResponse atualizar(@PathVariable Long id, @Valid @RequestBody TransactionRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
