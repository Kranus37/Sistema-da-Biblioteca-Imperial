package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma reserva de obra
 * Corresponde à tabela 'reservas' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "reservas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @Column(name = "id_reserva", length = 30)
    private String idReserva;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_obra", nullable = false)
    private Obra obra;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "data_reserva", nullable = false, updatable = false)
    private LocalDateTime dataReserva;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_reserva")
    private StatusReserva statusReserva = StatusReserva.ATIVA;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDate dataExpiracao;

    @Column(name = "data_atendimento")
    private LocalDateTime dataAtendimento;

    @PrePersist
    protected void onCreate() {
        if (dataReserva == null) {
            dataReserva = LocalDateTime.now();
        }
        if (statusReserva == null) {
            statusReserva = StatusReserva.ATIVA;
        }
    }

    public enum StatusReserva {
        ATIVA("Ativa"),
        ATENDIDA("Atendida"),
        CANCELADA("Cancelada"),
        EXPIRADA("Expirada");

        private final String descricao;

        StatusReserva(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
