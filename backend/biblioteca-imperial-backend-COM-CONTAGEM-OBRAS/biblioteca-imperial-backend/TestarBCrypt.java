import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Classe de teste para validar hash BCrypt
 * 
 * Execute com: java TestarBCrypt.java (Java 11+)
 */
public class TestarBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String senhaTexto = "senha123";
        String hashDoBanco = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        System.out.println("=== TESTE DE VALIDAÇÃO BCRYPT ===");
        System.out.println("Senha em texto: " + senhaTexto);
        System.out.println("Hash do banco: " + hashDoBanco);
        System.out.println();
        
        boolean valido = encoder.matches(senhaTexto, hashDoBanco);
        
        System.out.println("Resultado da validação: " + (valido ? "✓ VÁLIDO" : "✗ INVÁLIDO"));
        System.out.println();
        
        if (!valido) {
            System.out.println("PROBLEMA: O hash não corresponde à senha!");
            System.out.println("Gerando novo hash...");
            String novoHash = encoder.encode(senhaTexto);
            System.out.println("Novo hash: " + novoHash);
            System.out.println();
            System.out.println("Execute no MySQL:");
            System.out.println("UPDATE usuarios SET senha_hash = '" + novoHash + "' WHERE email = 'samuel.resende@biblioimp.org';");
        }
    }
}
