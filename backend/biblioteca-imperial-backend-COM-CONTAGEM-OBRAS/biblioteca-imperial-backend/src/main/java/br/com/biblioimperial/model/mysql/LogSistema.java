package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidade que representa um log de ação do sistema
 * Corresponde à tabela 'logs_sistema' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "logs_sistema")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long idLog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "acao", length = 100, nullable = false)
    private String acao;

    @Column(name = "tabela_afetada", length = 50)
    private String tabelaAfetada;

    @Column(name = "registro_afetado", length = 30)
    private String registroAfetado;

    @Column(name = "detalhes", columnDefinition = "TEXT")
    private String detalhes;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @PrePersist
    protected void onCreate() {
        if (dataHora == null) {
            dataHora = LocalDateTime.now();
        }
    }
}
