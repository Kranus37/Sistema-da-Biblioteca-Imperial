package br.com.biblioimperial.dto;

import br.com.biblioimperial.model.mysql.Categoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para Categoria com informações adicionais
 * Inclui a contagem de obras da categoria
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    
    private String idCategoria;
    private String nomeCategoria;
    private String descricao;
    private Integer nivelRestricao;
    private LocalDateTime dataCriacao;
    private Long totalObras;
    
    /**
     * Construtor a partir de uma entidade Categoria
     */
    public CategoriaDTO(Categoria categoria, Long totalObras) {
        this.idCategoria = categoria.getIdCategoria();
        this.nomeCategoria = categoria.getNomeCategoria();
        this.descricao = categoria.getDescricao();
        this.nivelRestricao = categoria.getNivelRestricao();
        this.dataCriacao = categoria.getDataCriacao();
        this.totalObras = totalObras;
    }
}
