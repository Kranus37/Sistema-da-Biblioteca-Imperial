package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um autor de obras
 * Corresponde à tabela 'autores' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Autor {

    @Id
    @Column(name = "id_autor", length = 20)
    private String idAutor;

    @Column(name = "nome_autor", length = 200, nullable = false)
    private String nomeAutor;

    @Column(name = "biografia", columnDefinition = "TEXT")
    private String biografia;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "data_falecimento")
    private LocalDate dataFalecimento;

    @Column(name = "nacionalidade", length = 100)
    private String nacionalidade;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    protected void onCreate() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
    }
}
