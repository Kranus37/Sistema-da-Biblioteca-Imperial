package br.com.biblioimperial.dto;

import br.com.biblioimperial.model.mysql.Exemplar;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO para transferência de dados de Exemplar
 * Evita problemas de serialização JSON com relações circulares
 * 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExemplarDTO {
    
    private String idExemplar;
    private String idObra;
    private String tituloObra;
    private String codigoBarras;
    private String estadoConservacao;
    private Boolean disponivel;
    private LocalDate dataAquisicao;
    private BigDecimal valorAquisicao;
    private String observacoes;
    
    /**
     * Converte uma entidade Exemplar para DTO
     */
    public static ExemplarDTO fromEntity(Exemplar exemplar) {
        ExemplarDTO dto = new ExemplarDTO();
        dto.setIdExemplar(exemplar.getIdExemplar());
        dto.setIdObra(exemplar.getObra().getIdObra());
        dto.setTituloObra(exemplar.getObra().getTitulo());
        dto.setCodigoBarras(exemplar.getCodigoBarras());
        dto.setEstadoConservacao(exemplar.getEstadoConservacao() != null ? 
            exemplar.getEstadoConservacao().getDescricao() : null);
        dto.setDisponivel(exemplar.getDisponivel());
        dto.setDataAquisicao(exemplar.getDataAquisicao());
        dto.setValorAquisicao(exemplar.getValorAquisicao());
        dto.setObservacoes(exemplar.getObservacoes());
        return dto;
    }
}
