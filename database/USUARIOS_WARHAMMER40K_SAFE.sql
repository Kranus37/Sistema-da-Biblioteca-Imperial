-- USUÁRIOS TEMÁTICOS WARHAMMER 40K - VERSÃO SAFE MODE

USE biblioteca_imperial;

-- Desabilita temporariamente o safe mode
SET SQL_SAFE_UPDATES = 0;

-- ============================================================================
-- PARTE 1: ATUALIZAR NOMES DOS GRUPOS (Hierarquia Imperial)
-- ============================================================================

UPDATE grupos_usuarios 
SET 
    nome_grupo = 'Senhores da Biblioteca',
    descricao = 'Custódios supremos do conhecimento imperial. Autoridade total sobre todos os arquivos sagrados.'
WHERE nivel_acesso = 5;

UPDATE grupos_usuarios 
SET 
    nome_grupo = 'Lexicanum',
    descricao = 'Mestres bibliotecários responsáveis pela catalogação e preservação das obras imperiais.'
WHERE nivel_acesso = 4;

UPDATE grupos_usuarios 
SET 
    nome_grupo = 'Escribas Imperiais',
    descricao = 'Servos dedicados ao atendimento e registro de empréstimos da biblioteca.'
WHERE nivel_acesso = 3;

UPDATE grupos_usuarios 
SET 
    nome_grupo = 'Scholam Progenium',
    descricao = 'Estudiosos de elite com acesso privilegiado ao acervo imperial.'
WHERE nivel_acesso = 2;

UPDATE grupos_usuarios 
SET 
    nome_grupo = 'Servos do Conhecimento',
    descricao = 'Cidadãos imperiais com acesso básico ao acervo público da biblioteca.'
WHERE nivel_acesso = 1;

-- ============================================================================
-- PARTE 2: LIMPAR USUÁRIOS ANTIGOS (Exceto os autores do trabalho)
-- ============================================================================

-- Remove todos os usuários EXCETO os 4 autores do trabalho
DELETE FROM usuarios 
WHERE email NOT IN (
    'samuel.resende@biblioimp.org',
    'rafael.santos@biblioimp.org',
    'raphael.simao@biblioimp.org',
    'yurik.feitosa@biblioimp.org'
);

-- ============================================================================
-- PARTE 3: ATUALIZAR OS 4 AUTORES COM NOMES TEMÁTICOS
-- ============================================================================

-- Senha padrão: senha123
-- Hash BCrypt que funciona:
SET @senha_hash = '$2a$10$AxIb3nGsT5LO3N7pW2LM5.jCXBMnIHP3kzrucTPQuUOCroMbChFeG';

-- Atualiza senha de todos os autores
UPDATE usuarios 
SET senha_hash = @senha_hash
WHERE email IN (
    'samuel.resende@biblioimp.org',
    'rafael.santos@biblioimp.org',
    'raphael.simao@biblioimp.org',
    'yurik.feitosa@biblioimp.org'
);

-- ----------------------------------------------------------------------------
-- NÍVEL 5: Senhor da Biblioteca
-- Samuel Telles → Malcador, o Sigillita
-- ----------------------------------------------------------------------------
UPDATE usuarios 
SET 
    id_usuario = 'USR-LORD-001',
    nome_completo = 'Malcador, o Sigillita',
    email = 'malcador.sigillita@biblioimp.org',
    id_grupo = (SELECT id_grupo FROM grupos_usuarios WHERE nivel_acesso = 5 LIMIT 1)
WHERE email = 'samuel.resende@biblioimp.org';

-- ----------------------------------------------------------------------------
-- NÍVEL 4: Lexicanum
-- Rafael Machado → Tigurius, Bibliotecário-Chefe
-- ----------------------------------------------------------------------------
UPDATE usuarios 
SET 
    id_usuario = 'USR-LEX-001',
    nome_completo = 'Tigurius, Bibliotecário-Chefe',
    email = 'tigurius.librarian@biblioimp.org',
    id_grupo = (SELECT id_grupo FROM grupos_usuarios WHERE nivel_acesso = 4 LIMIT 1)
WHERE email = 'rafael.santos@biblioimp.org';

-- ----------------------------------------------------------------------------
-- NÍVEL 3: Escriba Imperial
-- Raphael Ryan → Adeptus Scribe Octavius
-- ----------------------------------------------------------------------------
UPDATE usuarios 
SET 
    id_usuario = 'USR-SCR-001',
    nome_completo = 'Adeptus Scribe Octavius',
    email = 'octavius.scribe@biblioimp.org',
    id_grupo = (SELECT id_grupo FROM grupos_usuarios WHERE nivel_acesso = 3 LIMIT 1)
WHERE email = 'raphael.simao@biblioimp.org';

-- ----------------------------------------------------------------------------
-- NÍVEL 2: Scholam Progenium
-- Yurik Alexsander → Inquisidor Gregor Eisenhorn
-- ----------------------------------------------------------------------------
UPDATE usuarios 
SET 
    id_usuario = 'USR-SCH-001',
    nome_completo = 'Inquisidor Gregor Eisenhorn',
    email = 'gregor.eisenhorn@biblioimp.org',
    id_grupo = (SELECT id_grupo FROM grupos_usuarios WHERE nivel_acesso = 2 LIMIT 1)
WHERE email = 'yurik.feitosa@biblioimp.org';

-- ============================================================================
-- PARTE 4: CRIAR O 5º USUÁRIO (Nível 1)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- NÍVEL 1: Servo do Conhecimento
-- Guardsman Lukas Bastonne
-- ----------------------------------------------------------------------------
INSERT INTO usuarios (id_usuario, nome_completo, email, senha_hash, id_grupo, ativo) VALUES
('USR-SRV-001', 
 'Guardsman Lukas Bastonne', 
 'lukas.bastonne@biblioimp.org', 
 @senha_hash, 
 (SELECT id_grupo FROM grupos_usuarios WHERE nivel_acesso = 1 LIMIT 1), 
 TRUE);

-- Reabilita o safe mode
SET SQL_SAFE_UPDATES = 1;

-- ============================================================================
-- PARTE 5: VERIFICAÇÃO DOS RESULTADOS
-- ============================================================================

-- Exibir grupos
SELECT 
    '═══════════════════════════════════════════════════════════' AS '';
SELECT 
    '                    GRUPOS IMPERIAIS                       ' AS '';
SELECT 
    '═══════════════════════════════════════════════════════════' AS '';

SELECT 
    nivel_acesso AS 'Nível',
    nome_grupo AS 'Grupo Imperial',
    descricao AS 'Descrição'
FROM grupos_usuarios
ORDER BY nivel_acesso DESC;

SELECT '' AS '';
SELECT 
    '═══════════════════════════════════════════════════════════' AS '';
SELECT 
    '                  USUÁRIOS DA HIERARQUIA                   ' AS '';
SELECT 
    '═══════════════════════════════════════════════════════════' AS '';

-- Exibir usuários por hierarquia
SELECT 
    g.nivel_acesso AS 'Nível',
    g.nome_grupo AS 'Grupo Imperial',
    u.nome_completo AS 'Nome',
    u.email AS 'Email de Acesso',
    CASE WHEN u.ativo THEN '✓ Ativo' ELSE '✗ Inativo' END AS 'Status'
FROM usuarios u
JOIN grupos_usuarios g ON u.id_grupo = g.id_grupo
ORDER BY g.nivel_acesso DESC;

SELECT '' AS '';
SELECT 
    '═══════════════════════════════════════════════════════════' AS '';
SELECT 
    '           TODOS OS USUÁRIOS - SENHA: senha123             ' AS '';
SELECT 
    '═══════════════════════════════════════════════════════════' AS '';

-- ============================================================================
-- CREDENCIAIS DE ACESSO
-- ============================================================================

/*

╔════════════════════════════════════════════════════════════════════════════╗
║                   BIBLIOTECA IMPERIAL - HIERARQUIA                         ║
║                        WARHAMMER 40,000                                    ║
╚════════════════════════════════════════════════════════════════════════════╝

┌────────────────────────────────────────────────────────────────────────────┐
│ NÍVEL 5 ⚜️  SENHORES DA BIBLIOTECA                                         │
├────────────────────────────────────────────────────────────────────────────┤
│ 👤 MALCADOR, O SIGILLITA                                                   │
│    📧 malcador.sigillita@biblioimp.org                                     │
│    🔐 senha123                                                             │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│ NÍVEL 4 📚 LEXICANUM                                                       │
├────────────────────────────────────────────────────────────────────────────┤
│ 👤 TIGURIUS, BIBLIOTECÁRIO-CHEFE                                           │
│    📧 tigurius.librarian@biblioimp.org                                     │
│    🔐 senha123                                                             │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│ NÍVEL 3 ✍️  ESCRIBAS IMPERIAIS                                             │
├────────────────────────────────────────────────────────────────────────────┤
│ 👤 ADEPTUS SCRIBE OCTAVIUS                                                 │
│    📧 octavius.scribe@biblioimp.org                                        │
│    🔐 senha123                                                             │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│ NÍVEL 2 🎓 SCHOLAM PROGENIUM                                               │
├────────────────────────────────────────────────────────────────────────────┤
│ 👤 INQUISIDOR GREGOR EISENHORN                                             │
│    📧 gregor.eisenhorn@biblioimp.org                                       │
│    🔐 senha123                                                             │
└────────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────────────┐
│ NÍVEL 1 👤 SERVOS DO CONHECIMENTO                                          │
├────────────────────────────────────────────────────────────────────────────┤
│ 👤 GUARDSMAN LUKAS BASTONNE                                                │
│    📧 lukas.bastonne@biblioimp.org                                         │
│    🔐 senha123                                                             │
└────────────────────────────────────────────────────────────────────────────┘
