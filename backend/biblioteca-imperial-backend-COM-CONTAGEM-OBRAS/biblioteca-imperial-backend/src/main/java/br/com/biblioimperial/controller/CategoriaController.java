package br.com.biblioimperial.controller;

import br.com.biblioimperial.dto.CategoriaDTO;
import br.com.biblioimperial.model.mysql.Categoria;
import br.com.biblioimperial.repository.mysql.CategoriaRepository;
import br.com.biblioimperial.repository.mysql.ObraRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller REST para operações com Categorias
 *
 */
@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final ObraRepository obraRepository;

    @GetMapping
    @Operation(summary = "Listar todas as categorias")
    public ResponseEntity<List<CategoriaDTO>> listarTodas() {
        List<CategoriaDTO> categorias = categoriaRepository.findAll().stream()
            .map(categoria -> {
                Long totalObras = obraRepository.countByCategoriaId(categoria.getIdCategoria());
                return new CategoriaDTO(categoria, totalObras);
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    public ResponseEntity<Categoria> buscarPorId(@PathVariable String id) {
        return categoriaRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nivel/{nivel}")
    @Operation(summary = "Buscar categorias por nível de restrição")
    public ResponseEntity<List<Categoria>> buscarPorNivel(@PathVariable Integer nivel) {
        return ResponseEntity.ok(categoriaRepository.findByNivelRestricao(nivel));
    }

    @PostMapping
    @Operation(summary = "Criar nova categoria")
    public ResponseEntity<Categoria> criar(@RequestBody Categoria categoria) {
        // Gerar ID se não fornecido
        if (categoria.getIdCategoria() == null || categoria.getIdCategoria().isEmpty()) {
            String novoId = gerarIdCategoria();
            categoria.setIdCategoria(novoId);
        }
        Categoria categoriaSalva = categoriaRepository.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria existente")
    public ResponseEntity<Categoria> atualizar(@PathVariable String id, @RequestBody Categoria categoria) {
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoria.setIdCategoria(id);
        return ResponseEntity.ok(categoriaRepository.save(categoria));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar categoria")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gera um ID único para a categoria
     */
    private String gerarIdCategoria() {
        long count = categoriaRepository.count() + 1;
        return String.format("CAT-%03d", count);
    }
}
