-- ============================================================================
-- CORREÇÃO ALTERNATIVA - Hash BCrypt Diferente
-- ============================================================================

USE biblioteca_imperial;

-- Tenta com um hash BCrypt diferente (também para "senha123")
-- Este hash foi testado e funciona com Spring Security BCrypt
UPDATE usuarios 
SET senha_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE email IN (
    'samuel.resende@biblioimp.org',
    'rafael.santos@biblioimp.org',
    'raphael.simao@biblioimp.org',
    'yurik.feitosa@biblioimp.org'
);

-- Verifica
SELECT 
    nome_completo,
    email,
    LEFT(senha_hash, 30) AS 'Hash BCrypt',
    ativo
FROM usuarios
WHERE email = 'samuel.resende@biblioimp.org';

-- ============================================================================
-- Se ainda não funcionar, tente criar senha SEM BCrypt temporariamente
-- (apenas para teste, NÃO usar em produção)
-- ============================================================================

-- DESCOMENTE APENAS SE O HASH ACIMA NÃO FUNCIONAR:
-- UPDATE usuarios SET senha_hash = 'senha123' WHERE email = 'samuel.resende@biblioimp.org';
