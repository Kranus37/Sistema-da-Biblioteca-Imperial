package br.com.biblioimperial.controller;

import br.com.biblioimperial.dto.ExemplarDTO;
import br.com.biblioimperial.model.mysql.Exemplar;
import br.com.biblioimperial.repository.mysql.ExemplarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/exemplares")
@CrossOrigin(origins = "*")
public class ExemplarController {

    @Autowired
    private ExemplarRepository exemplarRepository;

    /**
     * Lista todos os exemplares
     */
    @GetMapping
    public ResponseEntity<List<ExemplarDTO>> listarTodos() {
        List<ExemplarDTO> exemplares = exemplarRepository.findAll()
                .stream()
                .map(ExemplarDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(exemplares);
    }

    /**
     * Busca exemplares de uma obra específica
     */
    @GetMapping("/obra/{idObra}")
    public ResponseEntity<List<ExemplarDTO>> listarPorObra(@PathVariable String idObra) {
        List<ExemplarDTO> exemplares = exemplarRepository.findByObra_IdObra(idObra)
                .stream()
                .map(ExemplarDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(exemplares);
    }

    /**
     * Busca exemplares DISPONÍVEIS de uma obra específica
     * Endpoint usado pelo frontend para verificar disponibilidade
     */
    @GetMapping("/obra/{idObra}/disponiveis")
    public ResponseEntity<List<ExemplarDTO>> listarDisponiveisPorObra(@PathVariable String idObra) {
        List<ExemplarDTO> exemplares = exemplarRepository.findByObra_IdObraAndDisponivelTrue(idObra)
                .stream()
                .map(ExemplarDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(exemplares);
    }

    /**
     * Busca um exemplar por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExemplarDTO> buscarPorId(@PathVariable String id) {
        return exemplarRepository.findById(id)
                .map(ExemplarDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo exemplar
     */
    @PostMapping
    public ResponseEntity<ExemplarDTO> criar(@RequestBody Exemplar exemplar) {
        Exemplar novoExemplar = exemplarRepository.save(exemplar);
        return ResponseEntity.ok(ExemplarDTO.fromEntity(novoExemplar));
    }

    /**
     * Atualiza um exemplar existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExemplarDTO> atualizar(@PathVariable String id, @RequestBody Exemplar exemplar) {
        if (!exemplarRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        exemplar.setIdExemplar(id);
        Exemplar exemplarAtualizado = exemplarRepository.save(exemplar);
        return ResponseEntity.ok(ExemplarDTO.fromEntity(exemplarAtualizado));
    }

    /**
     * Deleta um exemplar
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        if (!exemplarRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        exemplarRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
