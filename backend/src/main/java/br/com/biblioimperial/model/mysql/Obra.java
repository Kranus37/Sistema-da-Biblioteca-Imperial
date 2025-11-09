package br.com.biblioimperial.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa uma obra catalogada
 * Corresponde à tabela 'obras' no banco de dados MySQL
 *
 */
@Entity
@Table(name = "obras")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Obra {

    @Id
    @Column(name = "id_obra", length = 30)
    private String idObra;

    @Column(name = "titulo", length = 300, nullable = false)
    private String titulo;

    @Column(name = "subtitulo", length = 300)
    private String subtitulo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "isbn", length = 20, unique = true)
    private String isbn;

    @Column(name = "ano_publicacao")
    private Integer anoPublicacao;

    @Column(name = "editora", length = 150)
    private String editora;

    @Column(name = "edicao", length = 50)
    private String edicao;

    @Column(name = "idioma", length = 50)
    private String idioma = "Português";

    @Column(name = "num_paginas")
    private Integer numPaginas;

    @Column(name = "sinopse", columnDefinition = "TEXT")
    private String sinopse;

    @Column(name = "localizacao_fisica", length = 100)
    private String localizacaoFisica;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @ManyToMany
    @JoinTable(
        name = "obras_autores",
        joinColumns = @JoinColumn(name = "id_obra"),
        inverseJoinColumns = @JoinColumn(name = "id_autor")
    )
    private Set<Autor> autores = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
        if (dataAtualizacao == null) {
            dataAtualizacao = LocalDateTime.now();
        }
        if (ativo == null) {
            ativo = true;
        }
        if (idioma == null) {
            idioma = "Português";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
