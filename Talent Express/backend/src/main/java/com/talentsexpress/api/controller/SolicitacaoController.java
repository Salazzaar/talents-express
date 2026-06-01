package com.talentsexpress.api.controller;

import com.talentsexpress.api.dto.CriarSolicitacaoDTO;
import com.talentsexpress.api.service.SolicitacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * API/Controller — Solicitações de Serviço
 * RF07: Controle de concorrência no aceite (Optimistic Locking)
 * RN03: Cancelamento com mínimo de 6h de antecedência
 */
@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitacaoController {

    private final SolicitacaoService solicitacaoService;

    /** POST /api/solicitacoes — Cliente cria solicitação */
    @PostMapping
    public ResponseEntity<?> criar(
            @Valid @RequestBody CriarSolicitacaoDTO dto,
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.status(201).body(solicitacaoService.criar(dto, userId));
    }

    /** GET /api/solicitacoes/cliente — Pedidos do cliente logado */
    @GetMapping("/cliente")
    public ResponseEntity<?> listarCliente(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(solicitacaoService.listarPorCliente(userId));
    }

    /** GET /api/solicitacoes/prestador — Chamados do prestador logado */
    @GetMapping("/prestador")
    public ResponseEntity<?> listarPrestador(@RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(solicitacaoService.listarPorPrestador(userId));
    }

    /** GET /api/solicitacoes/{id} — Detalhes de uma solicitação */
    @GetMapping("/{id}")
    public ResponseEntity<?> detalhes(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.buscarPorId(id));
    }

    /**
     * PATCH /api/solicitacoes/{id}/aceitar
     * RF07: Optimistic Locking — trata conflito de concorrência
     */
    @PatchMapping("/{id}/aceitar")
    public ResponseEntity<?> aceitar(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        try {
            return ResponseEntity.ok(solicitacaoService.aceitar(id, userId));
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(409).body(
                new ErrorResponse("RF07: Conflito — esta solicitação já foi aceita por outro prestador."));
        }
    }

    /** PATCH /api/solicitacoes/{id}/recusar */
    @PatchMapping("/{id}/recusar")
    public ResponseEntity<?> recusar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.recusar(id));
    }

    /**
     * PATCH /api/solicitacoes/{id}/cancelar
     * RN03: Bloqueia cancelamento com < 6h de antecedência
     */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.cancelar(id));
    }

    /** PATCH /api/solicitacoes/{id}/concluir */
    @PatchMapping("/{id}/concluir")
    public ResponseEntity<?> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(solicitacaoService.concluir(id));
    }

    record ErrorResponse(String message) {}
}
