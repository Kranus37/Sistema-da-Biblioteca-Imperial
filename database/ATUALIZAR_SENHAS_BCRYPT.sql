-- ============================================================================
-- SCRIPT PARA ATUALIZAR SENHAS COM HASH BCRYPT
-- Biblioteca Imperial

USE biblioteca_imperial;

-- Hash BCrypt para "senha123" (gerado com BCrypt strength 10)
-- Senha original: senha123
-- Hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

UPDATE usuarios 
SET senha_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE email IN (
    'samuel.resende@biblioimp.org',
    'rafael.santos@biblioimp.org',
    'raphael.simao@biblioimp.org',
    'yurik.feitosa@biblioimp.org'
);

-- Verifica as atualizações
SELECT 
    id_usuario,
    nome_completo,
    email,
    LEFT(senha_hash, 20) AS 'Hash (primeiros 20 chars)',
    ativo
FROM usuarios
WHERE email IN (
    'samuel.resende@biblioimp.org',
    'rafael.santos@biblioimp.org',
    'raphael.simao@biblioimp.org',
    'yurik.feitosa@biblioimp.org'
);

-- ============================================================================
-- INFORMAÇÕES IMPORTANTES
-- ============================================================================
-- 
-- Todos os 4 usuários agora têm a senha: senha123
-- 
-- Hash BCrypt usado: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- 
-- Para gerar novos hashes BCrypt em Java:
-- BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
-- String hash = encoder.encode("sua_senha");
-- ============================================================================
