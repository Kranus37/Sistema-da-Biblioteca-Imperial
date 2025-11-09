package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa uma categoria de obras
 * Corresponde à tabela 'categorias' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "categorias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria {

    @Id
    @Column(name = "id_categoria", length = 20)
    private String idCategoria;

    @Column(name = "nome_categoria", length = 100, nullable = false, unique = true)
    private String nomeCategoria;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "nivel_restricao")
    private Integer nivelRestricao = 0;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (nivelRestricao == null) {
            nivelRestricao = 0;
        }
    }
}
