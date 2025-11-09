package br.com.biblioimperial.dto;

import br.com.biblioimperial.model.mysql.Autor;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para Autor com informações adicionais
 * Inclui a contagem de obras do autor
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutorDTO {
    
    private String idAutor;
    private String nomeAutor;
    private String biografia;
    private LocalDate dataNascimento;
    private LocalDate dataFalecimento;
    private String nacionalidade;
    private LocalDateTime dataCadastro;
    private Long totalObras;
    
    /**
     * Construtor a partir de uma entidade Autor
     */
    public AutorDTO(Autor autor, Long totalObras) {
        this.idAutor = autor.getIdAutor();
        this.nomeAutor = autor.getNomeAutor();
        this.biografia = autor.getBiografia();
        this.dataNascimento = autor.getDataNascimento();
        this.dataFalecimento = autor.getDataFalecimento();
        this.nacionalidade = autor.getNacionalidade();
        this.dataCadastro = autor.getDataCadastro();
        this.totalObras = totalObras;
    }
}
