package br.com.biblioimperial.dto;

import br.com.biblioimperial.model.mysql.Autor;
import br.com.biblioimperial.model.mysql.Categoria;

import java.util.List;
import java.util.Set;

/**
 * DTO para Obra com informações adicionais calculadas
 * 
 * @author Samuel Telles de Vasconcellos Resende
 * @author Rafael Machado dos Santos
 * @author Raphael Ryan Pires Simão
 * @author Yurik Alexsander Soares Feitosa
 */
public class ObraDTO {
    
    private String idObra;
    private String titulo;
    private String subtitulo;
    private String isbn;
    private Integer anoPublicacao;
    private String editora;
    private String idioma;
    private Integer numPaginas;
    private String sinopse;
    private String localizacaoFisica;
    private Categoria categoria;
    private Set<Autor> autores;
    private Long totalExemplares;
    
    // Construtores
    public ObraDTO() {
    }
    
    public ObraDTO(String idObra, String titulo, String subtitulo, String isbn, 
                   Integer anoPublicacao, String editora, String idioma, 
                   Integer numPaginas, String sinopse, String localizacaoFisica,
                   Categoria categoria, Set<Autor> autores, Long totalExemplares) {
        this.idObra = idObra;
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.isbn = isbn;
        this.anoPublicacao = anoPublicacao;
        this.editora = editora;
        this.idioma = idioma;
        this.numPaginas = numPaginas;
        this.sinopse = sinopse;
        this.localizacaoFisica = localizacaoFisica;
        this.categoria = categoria;
        this.autores = autores;
        this.totalExemplares = totalExemplares;
    }
    
    // Getters e Setters
    public String getIdObra() {
        return idObra;
    }
    
    public void setIdObra(String idObra) {
        this.idObra = idObra;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getSubtitulo() {
        return subtitulo;
    }
    
    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }
    
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public Integer getAnoPublicacao() {
        return anoPublicacao;
    }
    
    public void setAnoPublicacao(Integer anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }
    
    public String getEditora() {
        return editora;
    }
    
    public void setEditora(String editora) {
        this.editora = editora;
    }
    
    public String getIdioma() {
        return idioma;
    }
    
    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }
    
    public Integer getNumPaginas() {
        return numPaginas;
    }
    
    public void setNumPaginas(Integer numPaginas) {
        this.numPaginas = numPaginas;
    }
    
    public String getSinopse() {
        return sinopse;
    }
    
    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }
    
    public String getLocalizacaoFisica() {
        return localizacaoFisica;
    }
    
    public void setLocalizacaoFisica(String localizacaoFisica) {
        this.localizacaoFisica = localizacaoFisica;
    }
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public Set<Autor> getAutores() {
        return autores;
    }
    
    public void setAutores(Set<Autor> autores) {
        this.autores = autores;
    }
    
    public Long getTotalExemplares() {
        return totalExemplares;
    }
    
    public void setTotalExemplares(Long totalExemplares) {
        this.totalExemplares = totalExemplares;
    }
}
