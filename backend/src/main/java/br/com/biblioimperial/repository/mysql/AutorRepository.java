package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para operações de banco de dados com Autor
 *
 */
@Repository
public interface AutorRepository extends JpaRepository<Autor, String> {
    
    List<Autor> findByNomeAutorContainingIgnoreCase(String nomeAutor);
    
    List<Autor> findByNacionalidade(String nacionalidade);
}
