package com.talentsexpress.api.dto;

import jakarta.validation.constraints.*;

public record LoginRequestDTO(
        @Email @NotBlank String email,
        @NotBlank String senha
) {}
