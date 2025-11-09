package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.Obra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados com Obra
 *
 */
@Repository
public interface ObraRepository extends JpaRepository<Obra, String> {
    
    List<Obra> findByAtivoTrue();
    
    List<Obra> findByTituloContainingIgnoreCaseAndAtivoTrue(String titulo);
    
    List<Obra> findByCategoria_IdCategoriaAndAtivoTrue(String idCategoria);
    
    Optional<Obra> findByIsbn(String isbn);
    
    @Query("SELECT o FROM Obra o JOIN o.autores a WHERE a.idAutor = :idAutor AND o.ativo = true")
    List<Obra> findByAutorIdAndAtivoTrue(String idAutor);
    
    @Query("SELECT COUNT(o) FROM Obra o JOIN o.autores a WHERE a.idAutor = :idAutor AND o.ativo = true")
    Long countByAutorId(String idAutor);
    
    @Query("SELECT COUNT(o) FROM Obra o WHERE o.categoria.idCategoria = :idCategoria AND o.ativo = true")
    Long countByCategoriaId(String idCategoria);
}
