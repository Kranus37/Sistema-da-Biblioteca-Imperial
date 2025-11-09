package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um grupo de usuários no sistema
 * Corresponde à tabela 'grupos_usuarios' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "grupos_usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrupoUsuario {

    @Id
    @Column(name = "id_grupo", length = 20)
    private String idGrupo;

    @Column(name = "nome_grupo", length = 100, nullable = false, unique = true)
    private String nomeGrupo;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "nivel_acesso", nullable = false)
    private Integer nivelAcesso;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
        if (ativo == null) {
            ativo = true;
        }
    }
}
