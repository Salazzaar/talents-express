package com.talentsexpress.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade central de usuário.
 * Representa tanto Clientes quanto Prestadores de Serviço.
 */
@Entity
@Table(name = "usuarios")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Email @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(name = "cpf_cnpj", nullable = false, unique = true)
    private String cpfCnpj;

    @NotBlank
    private String senha; // BCrypt hash

    @NotNull
    @Enumerated(EnumType.STRING)
    private Perfil perfil;

    @NotNull
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    private String telefone;
    private String bairro;
    private String bio;

    @Builder.Default
    private boolean ativo = true;

    @Builder.Default
    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    // RN02: Indica se o perfil está completo
    @Builder.Default
    @Column(name = "perfil_completo")
    private boolean perfilCompleto = false;

    public enum Perfil { CLIENTE, PRESTADOR, ADMIN }
}
