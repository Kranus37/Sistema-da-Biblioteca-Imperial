package br.com.biblioimperial.repository.mongodb;

import br.com.biblioimperial.model.mongodb.RecomendacaoObra;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository MongoDB para operações com RecomendacaoObra
 *
 */
@Repository
public interface RecomendacaoObraRepository extends MongoRepository<RecomendacaoObra, String> {
    
    Optional<RecomendacaoObra> findByIdUsuario(String idUsuario);
    
    List<RecomendacaoObra> findByDataExpiracaoAfter(LocalDateTime dataAtual);
    
    List<RecomendacaoObra> findByDataExpiracaoBefore(LocalDateTime dataAtual);
    
    void deleteByDataExpiracaoBefore(LocalDateTime dataAtual);
}
