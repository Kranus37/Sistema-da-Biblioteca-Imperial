package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.LogSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para operações de banco de dados com LogSistema
 *
 */
@Repository
public interface LogSistemaRepository extends JpaRepository<LogSistema, Long> {
    
    List<LogSistema> findByUsuario_IdUsuario(String idUsuario);
    
    List<LogSistema> findByAcao(String acao);
    
    List<LogSistema> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
    
    List<LogSistema> findByTabelaAfetada(String tabelaAfetada);
}
