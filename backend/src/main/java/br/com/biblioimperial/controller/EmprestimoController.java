package br.com.biblioimperial.controller;

import br.com.biblioimperial.dto.EmprestimoDTO;
import br.com.biblioimperial.model.mysql.Emprestimo;
import br.com.biblioimperial.model.mysql.Exemplar;
import br.com.biblioimperial.model.mysql.Usuario;
import br.com.biblioimperial.repository.mysql.EmprestimoRepository;
import br.com.biblioimperial.repository.mysql.ExemplarRepository;
import br.com.biblioimperial.repository.mysql.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/emprestimos")
@CrossOrigin(origins = "*")
public class EmprestimoController {

    @Autowired
    private EmprestimoRepository emprestimoRepository;
    
    @Autowired
    private ExemplarRepository exemplarRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Lista todos os empréstimos
     */
    @GetMapping
    public ResponseEntity<List<EmprestimoDTO>> listarTodos() {
        List<EmprestimoDTO> emprestimos = emprestimoRepository.findAll()
                .stream()
                .map(EmprestimoDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * Busca empréstimos de um usuário específico
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<EmprestimoDTO>> listarPorUsuario(@PathVariable String idUsuario) {
        List<EmprestimoDTO> emprestimos = emprestimoRepository.findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(EmprestimoDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * Busca empréstimos ativos
     */
    @GetMapping("/ativos")
    public ResponseEntity<List<EmprestimoDTO>> listarAtivos() {
        List<EmprestimoDTO> emprestimos = emprestimoRepository
                .findByStatusEmprestimo(Emprestimo.StatusEmprestimo.ATIVO)
                .stream()
                .map(EmprestimoDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * Busca empréstimos atrasados
     */
    @GetMapping("/atrasados")
    public ResponseEntity<List<EmprestimoDTO>> listarAtrasados() {
        List<EmprestimoDTO> emprestimos = emprestimoRepository
                .findEmprestimosAtrasados(LocalDate.now())
                .stream()
                .map(EmprestimoDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(emprestimos);
    }

    /**
     * Conta empréstimos ativos
     */
    @GetMapping("/count/ativos")
    public ResponseEntity<Map<String, Long>> contarAtivos() {
        long count = emprestimoRepository
                .findByStatusEmprestimo(Emprestimo.StatusEmprestimo.ATIVO)
                .size();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Conta empréstimos atrasados
     */
    @GetMapping("/count/atrasados")
    public ResponseEntity<Map<String, Long>> contarAtrasados() {
        long count = emprestimoRepository
                .findEmprestimosAtrasados(LocalDate.now())
                .size();
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Busca um empréstimo por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoDTO> buscarPorId(@PathVariable String id) {
        return emprestimoRepository.findById(id)
                .map(EmprestimoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo empréstimo
     */
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, String> request) {
        return realizarEmprestimo(request);
    }

    /**
     * Realiza um empréstimo (alias para criar)
     */
    @PostMapping("/realizar")
    public ResponseEntity<?> realizarEmprestimo(@RequestBody Map<String, String> request) {
        try {
            String idExemplar = request.get("idExemplar");
            String idUsuario = request.get("idUsuario");
            
            // Validar exemplar
            Exemplar exemplar = exemplarRepository.findById(idExemplar)
                    .orElseThrow(() -> new RuntimeException("Exemplar não encontrado"));
            
            if (!exemplar.getDisponivel()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Exemplar não está disponível"));
            }
            
            // Validar usuário
            Usuario usuario = usuarioRepository.findById(idUsuario)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            // Criar empréstimo
            Emprestimo emprestimo = new Emprestimo();
            emprestimo.setIdEmprestimo("EMP-" + UUID.randomUUID().toString().substring(0, 8));
            emprestimo.setExemplar(exemplar);
            emprestimo.setUsuario(usuario);
            emprestimo.setDataEmprestimo(LocalDateTime.now());
            emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(14)); // 14 dias
            emprestimo.setStatusEmprestimo(Emprestimo.StatusEmprestimo.ATIVO);
            emprestimo.setRenovacoes(0);
            
            // Marcar exemplar como indisponível
            exemplar.setDisponivel(false);
            exemplarRepository.save(exemplar);
            
            // Salvar empréstimo
            Emprestimo novoEmprestimo = emprestimoRepository.save(emprestimo);
            
            return ResponseEntity.ok(EmprestimoDTO.fromEntity(novoEmprestimo));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Devolve um empréstimo
     */
    @RequestMapping(value = "/{id}/devolver", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<?> devolver(@PathVariable String id) {
        try {
            Emprestimo emprestimo = emprestimoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
            
            if (emprestimo.getStatusEmprestimo() != Emprestimo.StatusEmprestimo.ATIVO) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Empréstimo não está ativo"));
            }
            
            // Atualizar empréstimo
            emprestimo.setDataDevolucao(LocalDateTime.now());
            emprestimo.setStatusEmprestimo(Emprestimo.StatusEmprestimo.DEVOLVIDO);
            
            // Marcar exemplar como disponível
            Exemplar exemplar = emprestimo.getExemplar();
            exemplar.setDisponivel(true);
            exemplarRepository.save(exemplar);
            
            // Salvar empréstimo
            Emprestimo emprestimoAtualizado = emprestimoRepository.save(emprestimo);
            
            return ResponseEntity.ok(EmprestimoDTO.fromEntity(emprestimoAtualizado));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Renova um empréstimo
     */
    @PostMapping("/{id}/renovar")
    public ResponseEntity<?> renovar(@PathVariable String id) {
        try {
            Emprestimo emprestimo = emprestimoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
            
            if (emprestimo.getStatusEmprestimo() != Emprestimo.StatusEmprestimo.ATIVO) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Empréstimo não está ativo"));
            }
            
            if (emprestimo.getRenovacoes() >= 2) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Limite de renovações atingido"));
            }
            
            // Renovar por mais 7 dias
            emprestimo.setDataPrevistaDevolucao(
                emprestimo.getDataPrevistaDevolucao().plusDays(7)
            );
            emprestimo.setRenovacoes(emprestimo.getRenovacoes() + 1);
            
            Emprestimo emprestimoAtualizado = emprestimoRepository.save(emprestimo);
            
            return ResponseEntity.ok(EmprestimoDTO.fromEntity(emprestimoAtualizado));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
