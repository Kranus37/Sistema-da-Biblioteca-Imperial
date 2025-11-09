SELECT 
    nome_completo,
    email,
    LEFT(senha_hash, 30) AS 'Hash BCrypt'
FROM usuarios
LIMIT 5;