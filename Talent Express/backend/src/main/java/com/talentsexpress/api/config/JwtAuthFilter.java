package com.talentsexpress.api.config;

import com.talentsexpress.api.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro JWT — executado uma vez por requisição.
 * Extrai o token do header Authorization, valida e:
 *   1) Popula o SecurityContext (Spring Security)
 *   2) Define o atributo "userId" no request (usado com @RequestAttribute nos controllers)
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.isValid(token)) {
                String email = jwtUtil.extractEmail(token);

                usuarioRepository.findByEmail(email).ifPresent(user -> {
                    // 1) Popula SecurityContext
                    var auth = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getPerfil().name()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    // 2) Define atributo "userId" para @RequestAttribute nos controllers
                    request.setAttribute("userId", user.getId());
                });
            }
        }

        chain.doFilter(request, response);
    }
}
