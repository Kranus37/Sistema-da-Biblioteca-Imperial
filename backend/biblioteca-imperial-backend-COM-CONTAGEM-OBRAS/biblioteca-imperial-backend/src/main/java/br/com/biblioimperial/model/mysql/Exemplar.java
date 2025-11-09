package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um exemplar físico de uma obra
 * Corresponde à tabela 'exemplares' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "exemplares")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Exemplar {

    @Id
    @Column(name = "id_exemplar", length = 30)
    private String idExemplar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_obra", nullable = false)
    private Obra obra;

    @Column(name = "codigo_barras", length = 50, unique = true)
    private String codigoBarras;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_conservacao")
    private EstadoConservacao estadoConservacao = EstadoConservacao.BOM;

    @Column(name = "disponivel")
    private Boolean disponivel = true;

    @Column(name = "data_aquisicao")
    private LocalDate dataAquisicao;

    @Column(name = "valor_aquisicao", precision = 10, scale = 2)
    private BigDecimal valorAquisicao;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
        if (disponivel == null) {
            disponivel = true;
        }
        if (estadoConservacao == null) {
            estadoConservacao = EstadoConservacao.BOM;
        }
    }

    public enum EstadoConservacao {
        EXCELENTE("Excelente"),
        BOM("Bom"),
        REGULAR("Regular"),
        RUIM("Ruim"),
        PESSIMO("Péssimo");

        private final String descricao;

        EstadoConservacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
