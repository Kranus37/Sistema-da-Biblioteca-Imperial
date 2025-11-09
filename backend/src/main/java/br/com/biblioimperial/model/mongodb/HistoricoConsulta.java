package br.com.biblioimperial.model.mongodb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento MongoDB para armazenar histórico de consultas de usuários
 * 
 * Justificativa para uso do MongoDB:
 * O histórico de consultas é um dado não-estruturado que pode crescer rapidamente
 * e não possui relacionamentos complexos. O MongoDB oferece:
 * - Alta performance para inserções massivas
 * - Flexibilidade de schema para diferentes tipos de consulta
 * - Escalabilidade horizontal para grandes volumes de dados
 * - Consultas rápidas por índices em campos específicos
 * 
 * Este tipo de dado de auditoria e analytics se beneficia da natureza
 * document-oriented do MongoDB, evitando sobrecarregar o banco relacional
 * com dados históricos de alta volumetria.
 *
 */
@Document(collection = "historico_consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoConsulta {

    @Id
    private String id;

    private String idUsuario;
    
    private String nomeUsuario;
    
    private String emailUsuario;
    
    private String tipoConsulta; // "OBRA", "AUTOR", "CATEGORIA"
    
    private String termoBusca;
    
    private List<String> resultadosIds;
    
    private Integer quantidadeResultados;
    
    private LocalDateTime dataHoraConsulta;
    
    private String ipOrigem;
    
    private String userAgent;
    
    private Long tempoResposta; // em milissegundos
    
    private String filtrosAplicados;

    public HistoricoConsulta(String idUsuario, String nomeUsuario, String emailUsuario,
                            String tipoConsulta, String termoBusca, 
                            List<String> resultadosIds, String ipOrigem) {
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.tipoConsulta = tipoConsulta;
        this.termoBusca = termoBusca;
        this.resultadosIds = resultadosIds;
        this.quantidadeResultados = resultadosIds != null ? resultadosIds.size() : 0;
        this.dataHoraConsulta = LocalDateTime.now();
        this.ipOrigem = ipOrigem;
    }
}
