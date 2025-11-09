package br.com.biblioimperial.dto;

import br.com.biblioimperial.model.mysql.Emprestimo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de Emprestimo
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
public class EmprestimoDTO {
    
    private String idEmprestimo;
    private String idExemplar;
    private String idUsuario;
    private String nomeUsuario;
    private String idObra;
    private String tituloObra;
    private String codigoBarras;
    private LocalDateTime dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDateTime dataDevolucao;
    private Integer renovacoes;
    private String statusEmprestimo;
    private String observacoes;
    private Boolean atrasado;
    private Integer diasAtraso;
    
    /**
     * Converte uma entidade Emprestimo para DTO
     */
    public static EmprestimoDTO fromEntity(Emprestimo emprestimo) {
        EmprestimoDTO dto = new EmprestimoDTO();
        dto.setIdEmprestimo(emprestimo.getIdEmprestimo());
        dto.setIdExemplar(emprestimo.getExemplar().getIdExemplar());
        dto.setIdUsuario(emprestimo.getUsuario().getIdUsuario());
        dto.setNomeUsuario(emprestimo.getUsuario().getNomeCompleto());
        dto.setIdObra(emprestimo.getExemplar().getObra().getIdObra());
        dto.setTituloObra(emprestimo.getExemplar().getObra().getTitulo());
        dto.setCodigoBarras(emprestimo.getExemplar().getCodigoBarras());
        dto.setDataEmprestimo(emprestimo.getDataEmprestimo());
        dto.setDataPrevistaDevolucao(emprestimo.getDataPrevistaDevolucao());
        dto.setDataDevolucao(emprestimo.getDataDevolucao());
        dto.setRenovacoes(emprestimo.getRenovacoes());
        dto.setStatusEmprestimo(emprestimo.getStatusEmprestimo() != null ? 
            emprestimo.getStatusEmprestimo().name() : null);
        dto.setObservacoes(emprestimo.getObservacoes());
        
        // Calcular se está atrasado
        if (emprestimo.getDataDevolucao() == null && 
            emprestimo.getDataPrevistaDevolucao() != null) {
            LocalDate hoje = LocalDate.now();
            boolean atrasado = hoje.isAfter(emprestimo.getDataPrevistaDevolucao());
            dto.setAtrasado(atrasado);
            
            if (atrasado) {
                long dias = java.time.temporal.ChronoUnit.DAYS.between(
                    emprestimo.getDataPrevistaDevolucao(), hoje);
                dto.setDiasAtraso((int) dias);
            } else {
                dto.setDiasAtraso(0);
            }
        } else {
            dto.setAtrasado(false);
            dto.setDiasAtraso(0);
        }
        
        return dto;
    }
}
