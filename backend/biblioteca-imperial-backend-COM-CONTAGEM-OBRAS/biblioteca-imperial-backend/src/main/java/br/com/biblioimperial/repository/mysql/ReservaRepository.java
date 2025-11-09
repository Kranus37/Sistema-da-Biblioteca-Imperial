package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de banco de dados com Reserva
 *
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, String> {
    
    List<Reserva> findByUsuario_IdUsuario(String idUsuario);
    
    List<Reserva> findByObra_IdObra(String idObra);
    
    List<Reserva> findByStatusReserva(Reserva.StatusReserva status);
    
    List<Reserva> findByObra_IdObraAndStatusReserva(
        String idObra, 
        Reserva.StatusReserva status
    );
}
