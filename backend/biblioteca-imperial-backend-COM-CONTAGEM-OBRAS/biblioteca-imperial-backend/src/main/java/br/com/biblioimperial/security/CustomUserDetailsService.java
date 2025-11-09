package br.com.biblioimperial.security;

import br.com.biblioimperial.model.mysql.Usuario;
import br.com.biblioimperial.repository.mysql.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Serviço customizado para autenticação de usuários
 * 
 * Implementa a interface UserDetailsService do Spring Security para
 * integrar a autenticação com o banco de dados MySQL.
 *
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        if (!usuario.getAtivo()) {
            throw new UsernameNotFoundException("Usuário inativo: " + email);
        }

        // Cria a autoridade baseada no grupo do usuário
        String role = "ROLE_" + usuario.getGrupo().getNomeGrupo().toUpperCase().replace(" ", "_");
        
        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getSenhaHash())
            .authorities(Collections.singletonList(new SimpleGrantedAuthority(role)))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!usuario.getAtivo())
            .build();
    }
}
