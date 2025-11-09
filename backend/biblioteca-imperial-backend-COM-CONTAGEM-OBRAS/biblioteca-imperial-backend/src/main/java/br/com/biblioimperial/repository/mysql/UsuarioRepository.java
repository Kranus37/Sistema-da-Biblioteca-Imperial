package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados com Usuario
 *
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByAtivoTrue();
    
    List<Usuario> findByGrupo_IdGrupo(String idGrupo);
    
    boolean existsByEmail(String email);
}
