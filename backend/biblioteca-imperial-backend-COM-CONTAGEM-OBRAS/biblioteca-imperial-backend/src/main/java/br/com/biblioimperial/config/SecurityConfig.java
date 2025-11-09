package br.com.biblioimperial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de Segurança do Spring Security
 * 
 * Implementa autenticação básica e controle de acesso aos endpoints da API.
 * Para um projeto acadêmico de 4º semestre, mantemos a configuração simples
 * mas funcional, sem complexidade excessiva.
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Habilita CORS
            .cors(cors -> {})
            
            // Desabilita CSRF para facilitar testes (em produção, deve ser habilitado)
            .csrf(csrf -> csrf.disable())
            
            // Configuração de autorização de requisições
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sem autenticação)
                .requestMatchers(
                    "/api/auth/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-ui.html",
                    "/api/obras/**",
                    "/api/usuarios/**",
                    "/api/autores/**",
                    "/api/categorias/**",
                    "/api/relatorios/**",
                    "/api/emprestimos/**",
                    "/api/grupos/**"
                ).permitAll()
                
                // Demais endpoints requerem autenticação
                .anyRequest().authenticated()
            )
            
            // Configuração de sessão stateless (REST API)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Habilita HTTP Basic Authentication (simples para projeto acadêmico)
            .httpBasic(basic -> {});

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt para hash de senhas
        return new BCryptPasswordEncoder();
    }
}
