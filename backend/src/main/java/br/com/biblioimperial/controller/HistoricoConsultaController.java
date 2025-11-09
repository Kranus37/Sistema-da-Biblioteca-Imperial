package br.com.biblioimperial.controller;

import br.com.biblioimperial.model.mongodb.HistoricoConsulta;
import br.com.biblioimperial.service.HistoricoConsultaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller REST para operações com Histórico de Consultas (MongoDB)
 *
 */
@RestController
@RequestMapping("/api/historico-consultas")
@RequiredArgsConstructor
@Tag(name = "Histórico de Consultas", description = "Endpoints para histórico de consultas (MongoDB)")
@CrossOrigin(origins = "*")
public class HistoricoConsultaController {

    private final HistoricoConsultaService historicoService;

    @PostMapping
    @Operation(summary = "Registrar nova consulta no histórico")
    public ResponseEntity<HistoricoConsulta> registrar(@RequestBody HistoricoConsulta historico) {
        return ResponseEntity.ok(historicoService.registrarConsulta(historico));
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Buscar histórico de consultas de um usuário")
    public ResponseEntity<List<HistoricoConsulta>> buscarPorUsuario(@PathVariable String idUsuario) {
        return ResponseEntity.ok(historicoService.buscarHistoricoPorUsuario(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}/recentes")
    @Operation(summary = "Buscar últimas 10 consultas de um usuário")
    public ResponseEntity<List<HistoricoConsulta>> buscarRecentes(@PathVariable String idUsuario) {
        return ResponseEntity.ok(historicoService.buscarUltimasConsultasUsuario(idUsuario));
    }

    @GetMapping("/periodo")
    @Operation(summary = "Buscar consultas por período")
    public ResponseEntity<List<HistoricoConsulta>> buscarPorPeriodo(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim
    ) {
        return ResponseEntity.ok(historicoService.buscarConsultasPorPeriodo(inicio, fim));
    }

    @GetMapping("/usuario/{idUsuario}/contagem")
    @Operation(summary = "Contar consultas de um usuário")
    public ResponseEntity<Long> contarConsultas(@PathVariable String idUsuario) {
        return ResponseEntity.ok(historicoService.contarConsultasUsuario(idUsuario));
    }

    @GetMapping("/tipo/{tipoConsulta}")
    @Operation(summary = "Buscar consultas por tipo")
    public ResponseEntity<List<HistoricoConsulta>> buscarPorTipo(@PathVariable String tipoConsulta) {
        return ResponseEntity.ok(historicoService.buscarPorTipoConsulta(tipoConsulta));
    }
}
