package br.com.biblioimperial.repository.mongodb;

import br.com.biblioimperial.model.mongodb.HistoricoConsulta;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository MongoDB para operações com HistoricoConsulta
 *
 */
@Repository
public interface HistoricoConsultaRepository extends MongoRepository<HistoricoConsulta, String> {
    
    List<HistoricoConsulta> findByIdUsuario(String idUsuario);
    
    List<HistoricoConsulta> findByIdUsuarioOrderByDataHoraConsultaDesc(String idUsuario);
    
    List<HistoricoConsulta> findByTipoConsulta(String tipoConsulta);
    
    List<HistoricoConsulta> findByDataHoraConsultaBetween(LocalDateTime inicio, LocalDateTime fim);
    
    List<HistoricoConsulta> findTop10ByIdUsuarioOrderByDataHoraConsultaDesc(String idUsuario);
    
    long countByIdUsuario(String idUsuario);
}
