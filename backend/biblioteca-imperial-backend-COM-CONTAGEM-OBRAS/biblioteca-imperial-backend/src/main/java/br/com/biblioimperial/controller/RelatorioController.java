package br.com.biblioimperial.controller;

import br.com.biblioimperial.model.mysql.Emprestimo;
import br.com.biblioimperial.repository.mysql.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para relatórios e estatísticas
 *
 */
@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Endpoints para relatórios e estatísticas")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final UsuarioRepository usuarioRepository;
    private final ObraRepository obraRepository;
    private final EmprestimoRepository emprestimoRepository;
    private final ExemplarRepository exemplarRepository;
    private final CategoriaRepository categoriaRepository;

    @GetMapping("/estatisticas")
    @Operation(summary = "Obter estatísticas gerais do sistema")
    public ResponseEntity<EstatisticasDTO> obterEstatisticas() {
        EstatisticasDTO stats = new EstatisticasDTO();
        
        // Total de usuários ativos
        long totalUsuarios = usuarioRepository.findAll().stream()
            .filter(u -> u.getAtivo() != null && u.getAtivo())
            .count();
        stats.setTotalUsuarios(totalUsuarios);
        
        // Total de obras no acervo
        stats.setTotalObras(obraRepository.count());
        
        // Total de empréstimos ativos
        long emprestimosAtivos = emprestimoRepository.findAll().stream()
            .filter(e -> "ATIVO".equals(e.getStatusEmprestimo()))
            .count();
        stats.setTotalEmprestimosAtivos(emprestimosAtivos);
        
        // Total de empréstimos atrasados
        long atrasados = emprestimoRepository.findAll().stream()
            .filter(e -> "ATRASADO".equals(e.getStatusEmprestimo()))
            .count();
        stats.setTotalAtrasados(atrasados);
        
        // Total de exemplares
        stats.setTotalExemplares(exemplarRepository.count());
        
        // Total de categorias
        stats.setTotalCategorias(categoriaRepository.count());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/obras/populares")
    @Operation(summary = "Obter obras mais emprestadas")
    public ResponseEntity<?> obterObrasPopulares(@RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(obraRepository.findAll().stream().limit(limite).toList());
    }

    @GetMapping("/usuarios/ativos")
    @Operation(summary = "Obter usuários mais ativos")
    public ResponseEntity<?> obterUsuariosAtivos(@RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(usuarioRepository.findAll().stream()
            .filter(u -> u.getAtivo() != null && u.getAtivo())
            .limit(limite)
            .toList());
    }

    /**
     * DTO para estatísticas gerais
     */
    @Data
    public static class EstatisticasDTO {
        private Long totalUsuarios;
        private Long totalObras;
        private Long totalEmprestimosAtivos;
        private Long totalAtrasados;
        private Long totalExemplares;
        private Long totalCategorias;
    }
}
