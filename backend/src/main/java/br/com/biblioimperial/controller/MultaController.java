package br.com.biblioimperial.controller;

import br.com.biblioimperial.dto.MultaDTO;
import br.com.biblioimperial.model.mysql.Multa;
import br.com.biblioimperial.repository.mysql.MultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/multas")
@CrossOrigin(origins = "*")
public class MultaController {

    @Autowired
    private MultaRepository multaRepository;

    /**
     * Lista todas as multas
     */
    @GetMapping
    public ResponseEntity<List<MultaDTO>> listarTodas() {
        List<MultaDTO> multas = multaRepository.findAll()
                .stream()
                .map(MultaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(multas);
    }

    /**
     * Busca multas de um usuário específico
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<MultaDTO>> listarPorUsuario(@PathVariable String idUsuario) {
        List<MultaDTO> multas = multaRepository.findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(MultaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(multas);
    }

    /**
     * Busca multas pendentes de um usuário
     */
    @GetMapping("/usuario/{idUsuario}/pendentes")
    public ResponseEntity<List<MultaDTO>> listarPendentesPorUsuario(@PathVariable String idUsuario) {
        List<MultaDTO> multas = multaRepository
                .findByUsuario_IdUsuarioAndStatusMulta(idUsuario, Multa.StatusMulta.PENDENTE)
                .stream()
                .map(MultaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(multas);
    }

    /**
     * Calcula total de multas pendentes de um usuário
     */
    @GetMapping("/usuario/{idUsuario}/total")
    public ResponseEntity<Map<String, BigDecimal>> calcularTotalPendentes(@PathVariable String idUsuario) {
        BigDecimal total = multaRepository.calcularTotalMultasPendentes(idUsuario);
        if (total == null) {
            total = BigDecimal.ZERO;
        }
        return ResponseEntity.ok(Map.of("total", total));
    }

    /**
     * Busca multas pendentes (todas)
     */
    @GetMapping("/pendentes")
    public ResponseEntity<List<MultaDTO>> listarPendentes() {
        List<MultaDTO> multas = multaRepository
                .findByStatusMulta(Multa.StatusMulta.PENDENTE)
                .stream()
                .map(MultaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(multas);
    }

    /**
     * Conta multas pendentes (todas)
     */
    @GetMapping("/pendentes/count")
    public ResponseEntity<Map<String, Long>> contarPendentes() {
        long count = multaRepository.findByStatusMulta(Multa.StatusMulta.PENDENTE).size();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Calcula valor total de multas pendentes (todas)
     */
    @GetMapping("/pendentes/total")
    public ResponseEntity<Map<String, BigDecimal>> calcularTotalGeralPendentes() {
        List<Multa> multas = multaRepository.findByStatusMulta(Multa.StatusMulta.PENDENTE);
        BigDecimal total = multas.stream()
                .map(Multa::getValorMulta)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(Map.of("total", total));
    }

    /**
     * Busca uma multa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MultaDTO> buscarPorId(@PathVariable String id) {
        return multaRepository.findById(id)
                .map(MultaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marca multa como paga
     */
    @PostMapping("/{id}/pagar")
    public ResponseEntity<MultaDTO> pagarMulta(@PathVariable String id) {
        return multaRepository.findById(id)
                .map(multa -> {
                    multa.setStatusMulta(Multa.StatusMulta.PAGA);
                    multa.setDataPagamento(java.time.LocalDateTime.now());
                    Multa multaAtualizada = multaRepository.save(multa);
                    return ResponseEntity.ok(MultaDTO.fromEntity(multaAtualizada));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cancela uma multa
     */
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<MultaDTO> cancelarMulta(@PathVariable String id) {
        return multaRepository.findById(id)
                .map(multa -> {
                    multa.setStatusMulta(Multa.StatusMulta.CANCELADA);
                    Multa multaAtualizada = multaRepository.save(multa);
                    return ResponseEntity.ok(MultaDTO.fromEntity(multaAtualizada));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
