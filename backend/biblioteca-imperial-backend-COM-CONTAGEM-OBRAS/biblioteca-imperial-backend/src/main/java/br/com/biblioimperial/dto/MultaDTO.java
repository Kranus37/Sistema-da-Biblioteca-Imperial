package br.com.biblioimperial.dto;

import br.com.biblioimperial.model.mysql.Multa;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de Multa
 * Evita problemas de serialização JSON com relações circulares
 * 
 * @author Samuel Telles de Vasconcellos Resende
 * @author Rafael Machado dos Santos
 * @author Raphael Ryan Pires Simão
 * @author Yurik Alexsander Soares Feitosa
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultaDTO {
    
    private String idMulta;
    private String idEmprestimo;
    private String idUsuario;
    private String nomeUsuario;
    private String tipoMulta;
    private BigDecimal valorMulta;
    private LocalDateTime dataMulta;
    private LocalDateTime dataPagamento;
    private String statusMulta;
    private String descricao;
    
    /**
     * Converte uma entidade Multa para DTO
     */
    public static MultaDTO fromEntity(Multa multa) {
        MultaDTO dto = new MultaDTO();
        dto.setIdMulta(multa.getIdMulta());
        dto.setIdEmprestimo(multa.getEmprestimo() != null ? 
            multa.getEmprestimo().getIdEmprestimo() : null);
        dto.setIdUsuario(multa.getUsuario().getIdUsuario());
        dto.setNomeUsuario(multa.getUsuario().getNomeCompleto());
        dto.setTipoMulta(multa.getTipoMulta() != null ? 
            multa.getTipoMulta().name() : null);
        dto.setValorMulta(multa.getValorMulta());
        dto.setDataMulta(multa.getDataMulta());
        dto.setDataPagamento(multa.getDataPagamento());
        dto.setStatusMulta(multa.getStatusMulta() != null ? 
            multa.getStatusMulta().name() : null);
        dto.setDescricao(multa.getDescricao());
        return dto;
    }
}
