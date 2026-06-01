package com.talentsexpress.api.controller;

import com.talentsexpress.api.dto.CriarAvaliacaoDTO;
import com.talentsexpress.api.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API/Controller — Avaliações (1-5 estrelas)
 */
@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarAvaliacaoDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.status(201).body(avaliacaoService.criar(dto, userId));
    }

    @GetMapping("/prestador/{prestadorId}")
    public ResponseEntity<?> listarPorPrestador(@PathVariable Long prestadorId) {
        return ResponseEntity.ok(avaliacaoService.listarPorPrestador(prestadorId));
    }

    @GetMapping("/prestador/{prestadorId}/media")
    public ResponseEntity<?> media(@PathVariable Long prestadorId) {
        return ResponseEntity.ok(avaliacaoService.mediaEContagem(prestadorId));
    }
}
