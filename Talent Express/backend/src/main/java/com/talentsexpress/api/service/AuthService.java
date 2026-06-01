package com.talentsexpress.api.service;

import com.talentsexpress.api.config.JwtUtil;
import com.talentsexpress.api.dto.*;
import com.talentsexpress.api.model.Usuario;
import com.talentsexpress.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

/**
 * Camada de Negócio — Autenticação e Cadastro
 * Aplica: RN01 (Maioridade para Prestadores)
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponseDTO register(RegisterRequestDTO dto) {
        // Validação: email já cadastrado
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new BusinessException("E-mail já cadastrado na plataforma.");
        }
        // Validação: CPF/CNPJ já cadastrado
        if (usuarioRepository.existsByCpfCnpj(dto.cpfCnpj())) {
            throw new BusinessException("CPF/CNPJ já cadastrado.");
        }

        // RN01 — Validação de Maioridade para Prestadores
        if (dto.perfil() == Usuario.Perfil.PRESTADOR) {
            int idade = Period.between(dto.dataNascimento(), LocalDate.now()).getYears();
            if (idade < 18) {
                throw new BusinessException("RN01: Prestadores devem ter 18 anos ou mais.");
            }
        }

        // perfilCompleto = true quando o prestador já preenche nome, telefone e bairro no cadastro
        // Sem isso, prestadores registrados via API nunca aparecem nas buscas (RN02)
        boolean perfilCompleto = dto.perfil() == Usuario.Perfil.PRESTADOR
                && dto.telefone() != null && !dto.telefone().isBlank()
                && dto.bairro()   != null && !dto.bairro().isBlank();

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .email(dto.email())
                .cpfCnpj(dto.cpfCnpj())
                .senha(passwordEncoder.encode(dto.senha()))
                .perfil(dto.perfil())
                .dataNascimento(dto.dataNascimento())
                .telefone(dto.telefone())
                .bairro(dto.bairro())
                .ativo(true)
                .perfilCompleto(perfilCompleto)
                .build();

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getPerfil().name());
        return new AuthResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getPerfil(), token);
    }

    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new BusinessException("Credenciais inválidas."));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new BusinessException("Credenciais inválidas.");
        }
        if (!usuario.isAtivo()) {
            throw new BusinessException("Conta desativada. Entre em contato com o suporte.");
        }

        String token = jwtUtil.generateToken(usuario.getEmail(), usuario.getPerfil().name());
        return new AuthResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(),
                usuario.getPerfil(), token);
    }
}
