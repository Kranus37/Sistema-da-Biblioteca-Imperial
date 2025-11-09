package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados com Categoria
 *
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, String> {
    
    Optional<Categoria> findByNomeCategoria(String nomeCategoria);
    
    List<Categoria> findByNivelRestricaoLessThanEqual(Integer nivelRestricao);
    
    List<Categoria> findByNivelRestricao(Integer nivelRestricao);
}
