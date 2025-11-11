

USE biblioteca_imperial;

UPDATE usuarios 
SET senha_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'
WHERE email IN (
    'samuel.resende@biblioimp.org',
    'rafael.santos@biblioimp.org',
    'raphael.simao@biblioimp.org',
    'yurik.feitosa@biblioimp.org'
);

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
