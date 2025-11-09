package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository para operações de banco de dados com Emprestimo
 *
 */
@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, String> {
    
    List<Emprestimo> findByUsuario_IdUsuario(String idUsuario);
    
    List<Emprestimo> findByStatusEmprestimo(Emprestimo.StatusEmprestimo status);
    
    List<Emprestimo> findByUsuario_IdUsuarioAndStatusEmprestimo(
        String idUsuario, 
        Emprestimo.StatusEmprestimo status
    );
    
    @Query("SELECT e FROM Emprestimo e WHERE e.dataPrevistaDevolucao < :data " +
           "AND e.statusEmprestimo = 'ATIVO'")
    List<Emprestimo> findEmprestimosAtrasados(LocalDate data);
    
    long countByUsuario_IdUsuarioAndStatusEmprestimo(
        String idUsuario, 
        Emprestimo.StatusEmprestimo status
    );
}
