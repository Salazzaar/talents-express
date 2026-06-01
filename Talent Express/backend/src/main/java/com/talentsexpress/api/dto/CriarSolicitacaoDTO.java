package com.talentsexpress.api.dto;

import java.time.LocalDateTime;

public record CriarSolicitacaoDTO(
        Long prestadorId,
        Long servicoId,
        String descricao,
        LocalDateTime dataHoraAgendada,
        String endereco
) {}
