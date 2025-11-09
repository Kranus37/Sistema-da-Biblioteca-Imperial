package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um usuário do sistema
 * Corresponde à tabela 'usuarios' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @Column(name = "id_usuario", length = 30)
    private String idUsuario;

    @Column(name = "nome_completo", length = 200, nullable = false)
    private String nomeCompleto;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "senha_hash", length = 255, nullable = false)
    private String senhaHash;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_grupo", nullable = false)
    private GrupoUsuario grupo;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @Column(name = "tentativas_login")
    private Integer tentativasLogin = 0;

    @PrePersist
    protected void onCreate() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
        if (ativo == null) {
            ativo = true;
        }
        if (tentativasLogin == null) {
            tentativasLogin = 0;
        }
    }
}
