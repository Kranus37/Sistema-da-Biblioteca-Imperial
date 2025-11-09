package br.com.biblioimperial.controller;

import br.com.biblioimperial.dto.ObraDTO;
import br.com.biblioimperial.model.mysql.Obra;
import br.com.biblioimperial.repository.mysql.ExemplarRepository;
import br.com.biblioimperial.service.ObraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações com Obras
 * 
 * @author Samuel Telles de Vasconcellos Resende
 * @author Rafael Machado dos Santos
 * @author Raphael Ryan Pires Simão
 * @author Yurik Alexsander Soares Feitosa
 */
@RestController
@RequestMapping("/api/obras")
@RequiredArgsConstructor
@Tag(name = "Obras", description = "Endpoints para gerenciamento de obras")
@CrossOrigin(origins = "*")
public class ObraController {

    private final ObraService obraService;
    private final ExemplarRepository exemplarRepository;

    @GetMapping
    @Operation(summary = "Listar todas as obras ativas")
    public ResponseEntity<List<ObraDTO>> listarTodas() {
        List<Obra> obras = obraService.listarTodasObrasAtivas();
        List<ObraDTO> obrasDTO = obras.stream()
            .map(obra -> {
                ObraDTO dto = new ObraDTO();
                dto.setIdObra(obra.getIdObra());
                dto.setTitulo(obra.getTitulo());
                dto.setSubtitulo(obra.getSubtitulo());
                dto.setIsbn(obra.getIsbn());
                dto.setAnoPublicacao(obra.getAnoPublicacao());
                dto.setEditora(obra.getEditora());
                dto.setIdioma(obra.getIdioma());
                dto.setNumPaginas(obra.getNumPaginas());
                dto.setSinopse(obra.getSinopse());
                dto.setLocalizacaoFisica(obra.getLocalizacaoFisica());
                dto.setCategoria(obra.getCategoria());
                dto.setAutores(obra.getAutores());
                dto.setTotalExemplares(exemplarRepository.countByObra_IdObra(obra.getIdObra()));
                return dto;
            })
            .toList();
        return ResponseEntity.ok(obrasDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar obra por ID")
    public ResponseEntity<Obra> buscarPorId(@PathVariable String id) {
        return obraService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar obras por título")
    public ResponseEntity<List<Obra>> buscarPorTitulo(@RequestParam String titulo) {
        return ResponseEntity.ok(obraService.buscarPorTitulo(titulo));
    }

    @GetMapping("/categoria/{idCategoria}")
    @Operation(summary = "Buscar obras por categoria")
    public ResponseEntity<List<Obra>> buscarPorCategoria(@PathVariable String idCategoria) {
        return ResponseEntity.ok(obraService.buscarPorCategoria(idCategoria));
    }

    @GetMapping("/autor/{idAutor}")
    @Operation(summary = "Buscar obras por autor")
    public ResponseEntity<List<Obra>> buscarPorAutor(@PathVariable String idAutor) {
        return ResponseEntity.ok(obraService.buscarPorAutor(idAutor));
    }

    @PostMapping
    @Operation(summary = "Criar nova obra")
    public ResponseEntity<Obra> criar(@RequestBody Obra obra) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(obraService.salvar(obra));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar obra existente")
    public ResponseEntity<Obra> atualizar(@PathVariable String id, @RequestBody Obra obra) {
        obra.setIdObra(id);
        return ResponseEntity.ok(obraService.salvar(obra));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Inativar obra")
    public ResponseEntity<Void> inativar(@PathVariable String id) {
        obraService.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
