package br.com.biblioimperial.dto;

import java.time.LocalDate;

// Usando Lombok para 'getters', 'setters', 'construtores'
// Se não estiver usando Lombok, crie-os manualmente.
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCreateDTO {
    
    // Apenas os campos que o usuário PODE enviar
    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String senha;
    
}
