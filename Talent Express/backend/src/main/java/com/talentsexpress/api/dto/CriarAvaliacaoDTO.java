package com.talentsexpress.api.dto;

import jakarta.validation.constraints.*;

public record CriarAvaliacaoDTO(
        @NotNull Long solicitacaoId,
        @NotNull @Min(1) @Max(5) Integer nota,
        String comentario
) {}
