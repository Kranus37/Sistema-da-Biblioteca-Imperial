package br.com.biblioimperial.controller;

import br.com.biblioimperial.model.mysql.Usuario;
import br.com.biblioimperial.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller para autenticação e operações de login
 *
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação de usuários")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Realizar login no sistema")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String senha = credentials.get("senha");

        Usuario usuario = usuarioService.buscarPorEmail(email)
            .orElse(null);

        // DEBUG: Log para diagnóstico
        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Email recebido: " + email);
        System.out.println("Senha recebida: " + senha);
        System.out.println("Usuário encontrado: " + (usuario != null ? usuario.getNomeCompleto() : "NULL"));
        if (usuario != null) {
            System.out.println("Hash do banco: " + usuario.getSenhaHash());
            System.out.println("Usuário ativo: " + usuario.getAtivo());
            boolean senhaValida = usuarioService.validarSenha(senha, usuario.getSenhaHash());
            System.out.println("Senha válida: " + senhaValida);
        }
        System.out.println("==================");

        if (usuario == null) {
            return ResponseEntity.status(401).body(Map.of("mensagem", "Credenciais inválidas"));
        }

        if (!usuario.getAtivo()) {
            return ResponseEntity.status(403).body(Map.of("mensagem", "Usuário inativo"));
        }

        if (!usuarioService.validarSenha(senha, usuario.getSenhaHash())) {
            return ResponseEntity.status(401).body(Map.of("mensagem", "Credenciais inválidas"));
        }

        // Retorna informações do usuário autenticado
        Map<String, Object> response = new HashMap<>();
        response.put("mensagem", "Login realizado com sucesso");
        response.put("usuario", Map.of(
            "id", usuario.getIdUsuario(),
            "nome", usuario.getNomeCompleto(),
            "email", usuario.getEmail(),
            "grupo", usuario.getGrupo().getNomeGrupo(),
            "nivelAcesso", usuario.getGrupo().getNivelAcesso()
        ));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/validar")
    @Operation(summary = "Validar sessão do usuário")
    public ResponseEntity<Map<String, String>> validar() {
        return ResponseEntity.ok(Map.of("mensagem", "Sessão válida"));
    }

    @PostMapping("/gerar-hash")
    @Operation(summary = "Gerar hash BCrypt para uma senha (APENAS PARA DEBUG)")
    public ResponseEntity<Map<String, String>> gerarHash(@RequestBody Map<String, String> request) {
        String senha = request.get("senha");
        String hash = usuarioService.gerarHashSenha(senha);
        
        return ResponseEntity.ok(Map.of(
            "senha", senha,
            "hash", hash,
            "sql", "UPDATE usuarios SET senha_hash = '" + hash + "' WHERE email = 'samuel.resende@biblioimp.org';"
        ));
    }
}
