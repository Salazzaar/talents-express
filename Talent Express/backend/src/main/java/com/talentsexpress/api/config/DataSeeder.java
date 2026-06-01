package com.talentsexpress.api.config;

import com.talentsexpress.api.model.Servico;
import com.talentsexpress.api.model.Usuario;
import com.talentsexpress.api.repository.ServicoRepository;
import com.talentsexpress.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Popula o banco de dados em memória (H2) com dados iniciais 
 * para que a busca funcione logo na primeira vez.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() > 0) {
            return; // Já populado
        }

        // 1. Criar um prestador (Eletricista em Alphaville)
        Usuario prestador1 = Usuario.builder()
                .nome("Carlos Silva")
                .email("carlos@teste.com")
                .cpfCnpj("111.111.111-11")
                .senha(passwordEncoder.encode("senha123"))
                .perfil(Usuario.Perfil.PRESTADOR)
                .dataNascimento(LocalDate.of(1985, 5, 20))
                .telefone("11988887777")
                .bairro("Alphaville")
                .ativo(true)
                .perfilCompleto(true)
                .build();
        usuarioRepository.save(prestador1);

        Servico servico1 = Servico.builder()
                .prestador(prestador1)
                .titulo("Eletricista Residencial e Predial")
                .descricao("Instalações, reparos, troca de fiação e quadros de força.")
                .categoria(Servico.Categoria.BRACAL)
                .precoHora(new BigDecimal("150.00"))
                .ativo(true)
                .diasDisponiveis(List.of("Segunda", "Terça", "Quarta", "Quinta", "Sexta"))
                .horaInicio("08:00")
                .horaFim("18:00")
                .build();
        servicoRepository.save(servico1);

        // 2. Criar um prestador (Professor particular no Centro)
        Usuario prestador2 = Usuario.builder()
                .nome("Ana Souza")
                .email("ana@teste.com")
                .cpfCnpj("222.222.222-22")
                .senha(passwordEncoder.encode("senha123"))
                .perfil(Usuario.Perfil.PRESTADOR)
                .dataNascimento(LocalDate.of(1992, 10, 15))
                .telefone("11977776666")
                .bairro("Centro")
                .ativo(true)
                .perfilCompleto(true)
                .build();
        usuarioRepository.save(prestador2);

        Servico servico2 = Servico.builder()
                .prestador(prestador2)
                .titulo("Aulas de Inglês Particular")
                .descricao("Inglês para negócios, conversação e reforço escolar.")
                .categoria(Servico.Categoria.INTELECTUAL)
                .precoHora(new BigDecimal("90.00"))
                .ativo(true)
                .diasDisponiveis(List.of("Segunda", "Quarta", "Sexta"))
                .horaInicio("18:00")
                .horaFim("22:00")
                .build();
        servicoRepository.save(servico2);
        
        // 3. Criar um cliente para testes
        Usuario cliente = Usuario.builder()
                .nome("Cliente Teste")
                .email("cliente@teste.com")
                .cpfCnpj("333.333.333-33")
                .senha(passwordEncoder.encode("senha123"))
                .perfil(Usuario.Perfil.CLIENTE)
                .dataNascimento(LocalDate.of(2000, 1, 1))
                .telefone("11955554444")
                .bairro("Centro")
                .ativo(true)
                .build();
        usuarioRepository.save(cliente);

        System.out.println("✅ Dados de teste inseridos no banco com sucesso!");
    }
}
