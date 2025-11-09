package br.com.biblioimperial.controller;

import br.com.biblioimperial.dto.AutorDTO;
import br.com.biblioimperial.model.mysql.Autor;
import br.com.biblioimperial.repository.mysql.AutorRepository;
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
 * Controller REST para operações com Autores
 *
 */
@RestController
@RequestMapping("/api/autores")
@RequiredArgsConstructor
@Tag(name = "Autores", description = "Endpoints para gerenciamento de autores")
@CrossOrigin(origins = "*")
public class AutorController {

    private final AutorRepository autorRepository;
    private final ObraRepository obraRepository;

    @GetMapping
    @Operation(summary = "Listar todos os autores")
    public ResponseEntity<List<AutorDTO>> listarTodos() {
        List<AutorDTO> autores = autorRepository.findAll().stream()
            .map(autor -> {
                Long totalObras = obraRepository.countByAutorId(autor.getIdAutor());
                return new AutorDTO(autor, totalObras);
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(autores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar autor por ID")
    public ResponseEntity<Autor> buscarPorId(@PathVariable String id) {
        return autorRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Criar novo autor")
    public ResponseEntity<Autor> criar(@RequestBody Autor autor) {
        // Gerar ID se não fornecido
        if (autor.getIdAutor() == null || autor.getIdAutor().isEmpty()) {
            String novoId = gerarIdAutor();
            autor.setIdAutor(novoId);
        }
        Autor autorSalvo = autorRepository.save(autor);
        return ResponseEntity.status(HttpStatus.CREATED).body(autorSalvo);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar autor existente")
    public ResponseEntity<Autor> atualizar(@PathVariable String id, @RequestBody Autor autor) {
        if (!autorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        autor.setIdAutor(id);
        return ResponseEntity.ok(autorRepository.save(autor));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar autor")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!autorRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        autorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Gera um ID único para o autor
     */
    private String gerarIdAutor() {
        long count = autorRepository.count() + 1;
        return String.format("AUT-%03d", count);
    }
}
