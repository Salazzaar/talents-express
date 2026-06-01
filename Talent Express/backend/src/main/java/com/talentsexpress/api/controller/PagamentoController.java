package com.talentsexpress.api.controller;

import com.talentsexpress.api.dto.IniciarPagamentoDTO;
import com.talentsexpress.api.service.PagamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API/Controller — Pagamentos (simulado / stub assíncrono)
 */
@RestController
@RequestMapping("/api/pagamentos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciar(@Valid @RequestBody IniciarPagamentoDTO dto) {
        return ResponseEntity.ok(pagamentoService.iniciar(dto));
    }

    @GetMapping("/{correlationId}/status")
    public ResponseEntity<?> status(@PathVariable String correlationId) {
        return ResponseEntity.ok(pagamentoService.consultarStatus(correlationId));
    }
}
