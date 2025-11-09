package br.com.biblioimperial.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Documento MongoDB para armazenar recomendações personalizadas de obras
 * 
 * Justificativa para uso do MongoDB:
 * O sistema de recomendações requer armazenamento de dados dinâmicos e
 * estruturas complexas (listas, mapas) que mudam frequentemente. O MongoDB é ideal para:
 * - Armazenar preferências de usuário em formato flexível
 * - Cache de recomendações calculadas para performance
 * - Dados temporários que expiram automaticamente (TTL)
 * - Estruturas aninhadas sem necessidade de múltiplas tabelas
 * 
 * Este tipo de funcionalidade de analytics e machine learning se beneficia
 * da flexibilidade do schema NoSQL, permitindo evolução rápida do modelo
 * de recomendação sem alterações de schema no banco relacional.
 *
 */
@Document(collection = "recomendacoes_obras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecomendacaoObra {

    @Id
    private String id;

    private String idUsuario;
    
    private List<ObraRecomendada> obrasRecomendadas;
    
    private Map<String, Integer> categoriasPreferidas; // categoria -> score
    
    private Map<String, Integer> autoresPreferidos; // autor -> score
    
    private LocalDateTime dataGeracao;
    
    private LocalDateTime dataExpiracao;
    
    private String algoritmoUtilizado;
    
    private Double scoreConfianca;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ObraRecomendada {
        private String idObra;
        private String tituloObra;
        private String categoriaObra;
        private Double scoreRecomendacao;
        private String motivoRecomendacao;
        private List<String> tagsRelacionadas;
    }
}
