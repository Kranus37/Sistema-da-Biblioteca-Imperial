package br.com.biblioimperial.service;

import br.com.biblioimperial.model.mysql.Emprestimo;
import br.com.biblioimperial.model.mysql.Exemplar;
import br.com.biblioimperial.model.mysql.Multa;
import br.com.biblioimperial.model.mysql.Usuario;
import br.com.biblioimperial.repository.mysql.EmprestimoRepository;
import br.com.biblioimperial.repository.mysql.ExemplarRepository;
import br.com.biblioimperial.repository.mysql.MultaRepository;
import br.com.biblioimperial.repository.mysql.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service para lógica de negócio relacionada a Empréstimos
 *
 */
@Service
@RequiredArgsConstructor
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final ExemplarRepository exemplarRepository;
    private final UsuarioRepository usuarioRepository;
    private final MultaRepository multaRepository;

    @Transactional
    public Emprestimo realizarEmprestimo(String idExemplar, String idUsuario, int diasEmprestimo) {
        // Busca o exemplar
        Exemplar exemplar = exemplarRepository.findById(idExemplar)
            .orElseThrow(() -> new RuntimeException("Exemplar não encontrado"));

        // Verifica disponibilidade
        if (!exemplar.getDisponivel()) {
            throw new RuntimeException("Exemplar não disponível para empréstimo");
        }

        // Busca o usuário
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Verifica se o usuário está ativo
        if (!usuario.getAtivo()) {
            throw new RuntimeException("Usuário inativo");
        }

        // Verifica se há multas pendentes
        long multasPendentes = multaRepository.countByUsuario_IdUsuarioAndStatusMulta(
            idUsuario, Multa.StatusMulta.PENDENTE
        );
        if (multasPendentes > 0) {
            throw new RuntimeException("Usuário possui multas pendentes");
        }

        // Cria o empréstimo
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setIdEmprestimo(gerarIdEmprestimo());
        emprestimo.setExemplar(exemplar);
        emprestimo.setUsuario(usuario);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(diasEmprestimo));
        emprestimo.setStatusEmprestimo(Emprestimo.StatusEmprestimo.ATIVO);

        // Marca o exemplar como indisponível
        exemplar.setDisponivel(false);
        exemplarRepository.save(exemplar);

        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo realizarDevolucao(String idEmprestimo) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (emprestimo.getStatusEmprestimo() == Emprestimo.StatusEmprestimo.DEVOLVIDO) {
            throw new RuntimeException("Empréstimo já foi devolvido");
        }

        // Calcula dias de atraso
        long diasAtraso = ChronoUnit.DAYS.between(
            emprestimo.getDataPrevistaDevolucao(), 
            LocalDate.now()
        );

        // Se houver atraso, gera multa
        if (diasAtraso > 0) {
            BigDecimal valorMulta = BigDecimal.valueOf(diasAtraso * 2.00);
            
            Multa multa = new Multa();
            multa.setIdMulta(gerarIdMulta());
            multa.setEmprestimo(emprestimo);
            multa.setUsuario(emprestimo.getUsuario());
            multa.setTipoMulta(Multa.TipoMulta.ATRASO);
            multa.setValorMulta(valorMulta);
            multa.setStatusMulta(Multa.StatusMulta.PENDENTE);
            multa.setDescricao("Multa por atraso de " + diasAtraso + " dia(s)");
            
            multaRepository.save(multa);
        }

        // Atualiza o empréstimo
        emprestimo.setDataDevolucao(LocalDateTime.now());
        emprestimo.setStatusEmprestimo(Emprestimo.StatusEmprestimo.DEVOLVIDO);

        // Libera o exemplar
        Exemplar exemplar = emprestimo.getExemplar();
        exemplar.setDisponivel(true);
        exemplarRepository.save(exemplar);

        return emprestimoRepository.save(emprestimo);
    }

    @Transactional
    public Emprestimo renovarEmprestimo(String idEmprestimo, int diasRenovacao) {
        Emprestimo emprestimo = emprestimoRepository.findById(idEmprestimo)
            .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        if (emprestimo.getStatusEmprestimo() != Emprestimo.StatusEmprestimo.ATIVO) {
            throw new RuntimeException("Apenas empréstimos ativos podem ser renovados");
        }

        if (emprestimo.getRenovacoes() >= 3) {
            throw new RuntimeException("Número máximo de renovações atingido");
        }

        emprestimo.setDataPrevistaDevolucao(
            emprestimo.getDataPrevistaDevolucao().plusDays(diasRenovacao)
        );
        emprestimo.setRenovacoes(emprestimo.getRenovacoes() + 1);

        return emprestimoRepository.save(emprestimo);
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimosUsuario(String idUsuario) {
        return emprestimoRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimoRepository.findByStatusEmprestimo(Emprestimo.StatusEmprestimo.ATIVO);
    }

    @Transactional(readOnly = true)
    public List<Emprestimo> listarEmprestimosAtrasados() {
        return emprestimoRepository.findEmprestimosAtrasados(LocalDate.now());
    }

    private String gerarIdEmprestimo() {
        return "EMP-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 3);
    }

    private String gerarIdMulta() {
        return "MLT-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 3);
    }
}
