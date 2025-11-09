package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.Exemplar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados com Exemplar
 * 
 * @author Samuel Telles de Vasconcellos Resende
 * @author Rafael Machado dos Santos
 * @author Raphael Ryan Pires Simão
 * @author Yurik Alexsander Soares Feitosa
 */
@Repository
public interface ExemplarRepository extends JpaRepository<Exemplar, String> {
    
    List<Exemplar> findByObra_IdObra(String idObra);
    
    List<Exemplar> findByObra_IdObraAndDisponivelTrue(String idObra);
    
    Optional<Exemplar> findByCodigoBarras(String codigoBarras);
    
    long countByObra_IdObraAndDisponivelTrue(String idObra);
    
    long countByObra_IdObra(String idObra);
}
