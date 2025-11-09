package br.com.biblioimperial.service;

import br.com.biblioimperial.model.mysql.Usuario;
import br.com.biblioimperial.repository.mysql.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service para lógica de negócio relacionada a Usuários
 *
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosAtivos() {
        return usuarioRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(String idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Transactional
    public Usuario salvar(Usuario usuario) {
        // Gera ID se não existir
        if (usuario.getIdUsuario() == null || usuario.getIdUsuario().isEmpty()) {
            usuario.setIdUsuario(gerarIdUsuario());
        }
        
        // Criptografa a senha se foi alterada
        if (usuario.getSenhaHash() != null && !usuario.getSenhaHash().startsWith("$2a$")) {
            usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        }
        
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void deletar(String idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    @Transactional
    public Usuario inativar(String idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(false);
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario ativar(String idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        usuario.setAtivo(true);
        return usuarioRepository.save(usuario);
    }

    /**
     * Valida se a senha em texto puro corresponde ao hash BCrypt armazenado
     */
    public boolean validarSenha(String senhaRaw, String senhaHash) {
        return passwordEncoder.matches(senhaRaw, senhaHash);
    }

    /**
     * Gera hash BCrypt para uma senha (usado para debug)
     */
    public String gerarHashSenha(String senhaRaw) {
        return passwordEncoder.encode(senhaRaw);
    }

    private String gerarIdUsuario() {
        return "USR-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
