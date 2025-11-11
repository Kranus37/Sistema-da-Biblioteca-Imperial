
USE biblioteca_imperial;

UPDATE usuarios 
SET senha_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE email IN (
    'samuel.resende@biblioimp.org',
    'rafael.santos@biblioimp.org',
    'raphael.simao@biblioimp.org',
    'yurik.feitosa@biblioimp.org'
);


SELECT 
    nome_completo,
    email,
    LEFT(senha_hash, 30) AS 'Hash BCrypt',
    ativo
FROM usuarios
WHERE email = 'samuel.resende@biblioimp.org';
