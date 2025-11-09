package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma multa aplicada a um usuário
 * Corresponde à tabela 'multas' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "multas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Multa {

    @Id
    @Column(name = "id_multa", length = 30)
    private String idMulta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_emprestimo")
    private Emprestimo emprestimo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_multa", nullable = false)
    private TipoMulta tipoMulta;

    @Column(name = "valor_multa", precision = 10, scale = 2, nullable = false)
    private BigDecimal valorMulta;

    @Column(name = "data_multa", nullable = false, updatable = false)
    private LocalDateTime dataMulta;

    @Column(name = "data_pagamento")
    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_multa")
    private StatusMulta statusMulta = StatusMulta.PENDENTE;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @PrePersist
    protected void onCreate() {
        if (dataMulta == null) {
            dataMulta = LocalDateTime.now();
        }
        if (statusMulta == null) {
            statusMulta = StatusMulta.PENDENTE;
        }
    }

    public enum TipoMulta {
        ATRASO("Atraso"),
        DANO("Dano"),
        PERDA("Perda"),
        OUTROS("Outros");

        private final String descricao;

        TipoMulta(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusMulta {
        PENDENTE("Pendente"),
        PAGA("Paga"),
        CANCELADA("Cancelada");

        private final String descricao;

        StatusMulta(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
