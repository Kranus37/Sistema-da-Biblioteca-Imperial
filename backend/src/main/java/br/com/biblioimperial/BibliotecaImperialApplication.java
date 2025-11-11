package br.com.biblioimperial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Classe principal da aplicação Biblioteca Imperial
 * 
 * Sistema de gerenciamento de biblioteca com temática Warhammer 40k
 *
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.com.biblioimperial.repository.mysql")
@EnableMongoRepositories(basePackages = "br.com.biblioimperial.repository.mongodb")
public class BibliotecaImperialApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaImperialApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  BIBLIOTECA IMPERIAL - Sistema Iniciado");
        System.out.println("  Em nome do Imperador da Humanidade!");
        System.out.println("==============================================");
    }
}
