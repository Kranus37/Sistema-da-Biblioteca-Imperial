package br.com.biblioimperial.service;

import br.com.biblioimperial.model.mongodb.HistoricoConsulta;
import br.com.biblioimperial.repository.mongodb.HistoricoConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service para lógica de negócio relacionada ao Histórico de Consultas (MongoDB)
 *
 */
@Service
@RequiredArgsConstructor
public class HistoricoConsultaService {

    private final HistoricoConsultaRepository historicoRepository;

    public HistoricoConsulta registrarConsulta(HistoricoConsulta historico) {
        return historicoRepository.save(historico);
    }

    public List<HistoricoConsulta> buscarHistoricoPorUsuario(String idUsuario) {
        return historicoRepository.findByIdUsuarioOrderByDataHoraConsultaDesc(idUsuario);
    }

    public List<HistoricoConsulta> buscarUltimasConsultasUsuario(String idUsuario) {
        return historicoRepository.findTop10ByIdUsuarioOrderByDataHoraConsultaDesc(idUsuario);
    }

    public List<HistoricoConsulta> buscarConsultasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return historicoRepository.findByDataHoraConsultaBetween(inicio, fim);
    }

    public long contarConsultasUsuario(String idUsuario) {
        return historicoRepository.countByIdUsuario(idUsuario);
    }

    public List<HistoricoConsulta> buscarPorTipoConsulta(String tipoConsulta) {
        return historicoRepository.findByTipoConsulta(tipoConsulta);
    }
}
