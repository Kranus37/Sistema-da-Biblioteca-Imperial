package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um empréstimo de exemplar
 * Corresponde à tabela 'emprestimos' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "emprestimos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {

    @Id
    @Column(name = "id_emprestimo", length = 30)
    private String idEmprestimo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_exemplar", nullable = false)
    private Exemplar exemplar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "data_emprestimo", nullable = false, updatable = false)
    private LocalDateTime dataEmprestimo;

    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_devolucao")
    private LocalDateTime dataDevolucao;

    @Column(name = "renovacoes")
    private Integer renovacoes = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_emprestimo")
    private StatusEmprestimo statusEmprestimo = StatusEmprestimo.ATIVO;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @PrePersist
    protected void onCreate() {
        if (dataEmprestimo == null) {
            dataEmprestimo = LocalDateTime.now();
        }
        if (renovacoes == null) {
            renovacoes = 0;
        }
        if (statusEmprestimo == null) {
            statusEmprestimo = StatusEmprestimo.ATIVO;
        }
    }

    public enum StatusEmprestimo {
        ATIVO("Ativo"),
        DEVOLVIDO("Devolvido"),
        ATRASADO("Atrasado"),
        CANCELADO("Cancelado");

        private final String descricao;

        StatusEmprestimo(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
