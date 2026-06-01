package com.talentsexpress.api.controller;

import com.talentsexpress.api.dto.CriarServicoDTO;
import com.talentsexpress.api.service.MatchingEngine;
import com.talentsexpress.api.service.ServicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API/Controller — Serviços e Matching
 * Gerencia portfólio do prestador e busca de profissionais (RNF01).
 */
@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MatchingController {

    private final MatchingEngine matchingEngine;
    private final ServicoService servicoService;

    /** GET /api/servicos/buscar?texto=&categoria=&bairro= — RNF01: < 3s */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscar(
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String bairro) {
        // Normaliza strings vazias para null — evita que a query JPQL falhe
        // com parâmetros como ?categoria= (string vazia ≠ null no JPQL IS NULL)
        String textoParam    = (texto    != null && !texto.isBlank())    ? texto.trim()    : null;
        String categoriaParam = (categoria != null && !categoria.isBlank()) ? categoria.trim() : null;
        String bairroParam   = (bairro   != null && !bairro.isBlank())   ? bairro.trim()   : null;
        return ResponseEntity.ok(matchingEngine.buscar(textoParam, categoriaParam, bairroParam));
    }

    /** GET /api/servicos — Lista todos os serviços ativos (catálogo público) */
    @GetMapping
    public ResponseEntity<?> listarTodos() {
        return ResponseEntity.ok(servicoService.listarAtivos());
    }

    /** GET /api/servicos/meus — Serviços do prestador autenticado */
    @GetMapping("/meus")
    public ResponseEntity<?> meus(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(servicoService.listarPorPrestador(userId));
    }

    /** GET /api/servicos/{id} — Detalhes de um serviço */
    @GetMapping("/{id}")
    public ResponseEntity<?> detalhes(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    /** POST /api/servicos — Cria novo serviço (requer autenticação de PRESTADOR) */
    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarServicoDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.status(201).body(servicoService.criar(dto, userId));
    }

    /** PUT /api/servicos/{id} — Atualiza serviço existente */
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody CriarServicoDTO dto) {
        return ResponseEntity.ok(servicoService.atualizar(id, dto));
    }

    /** DELETE /api/servicos/{id} — Remove serviço */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable Long id) {
        servicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
