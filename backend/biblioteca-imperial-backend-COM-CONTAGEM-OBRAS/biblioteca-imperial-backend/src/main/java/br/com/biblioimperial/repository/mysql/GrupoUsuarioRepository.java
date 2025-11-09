package br.com.biblioimperial.repository.mysql;

import br.com.biblioimperial.model.mysql.GrupoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados com GrupoUsuario
 *
 */
@Repository
public interface GrupoUsuarioRepository extends JpaRepository<GrupoUsuario, String> {
    
    Optional<GrupoUsuario> findByNomeGrupo(String nomeGrupo);
    
    List<GrupoUsuario> findByAtivoTrue();
    
    List<GrupoUsuario> findByNivelAcessoGreaterThanEqual(Integer nivelAcesso);
}
