package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.Multa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository para operações de banco de dados com Multa
 *
 */
@Repository
public interface MultaRepository extends JpaRepository<Multa, String> {
    
    List<Multa> findByUsuario_IdUsuario(String idUsuario);
    
    List<Multa> findByStatusMulta(Multa.StatusMulta status);
    
    List<Multa> findByUsuario_IdUsuarioAndStatusMulta(
        String idUsuario, 
        Multa.StatusMulta status
    );
    
    @Query("SELECT SUM(m.valorMulta) FROM Multa m WHERE m.usuario.idUsuario = :idUsuario " +
           "AND m.statusMulta = 'PENDENTE'")
    BigDecimal calcularTotalMultasPendentes(String idUsuario);
    
    long countByUsuario_IdUsuarioAndStatusMulta(
        String idUsuario, 
        Multa.StatusMulta status
    );
}
