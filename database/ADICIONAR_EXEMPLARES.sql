USE biblioteca_imperial;

INSERT INTO exemplares (id_exemplar, id_obra, codigo_barras, estado_conservacao, disponivel, data_aquisicao, valor_aquisicao, observacoes) VALUES
(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'Codex Astartes' LIMIT 1),
 CONCAT('978-1-84154-000-', FLOOR(RAND() * 10)),
 'Excelente',
 TRUE,
 '2025-01-15',
 150.00,
 'Exemplar principal - Acervo Geral'),

(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'Codex Astartes' LIMIT 1),
 CONCAT('978-1-84154-000-', FLOOR(RAND() * 10)),
 'Bom',
 TRUE,
 '2025-01-15',
 150.00,
 'Exemplar secundário - Acervo Geral'),

(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'Codex Astartes' LIMIT 1),
 CONCAT('978-1-84154-000-', FLOOR(RAND() * 10)),
 'Bom',
 TRUE,
 '2025-01-15',
 150.00,
 'Exemplar terciário - Reserva Técnica');

INSERT INTO exemplares (id_exemplar, id_obra, codigo_barras, estado_conservacao, disponivel, data_aquisicao, valor_aquisicao, observacoes) VALUES
(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'A Heresia de Horus' LIMIT 1),
 CONCAT('978-1-84154-001-', FLOOR(RAND() * 10)),
 'Excelente',
 TRUE,
 '2025-02-10',
 200.00,
 'Edição especial - Acervo Restrito'),

(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'A Heresia de Horus' LIMIT 1),
 CONCAT('978-1-84154-001-', FLOOR(RAND() * 10)),
 'Bom',
 TRUE,
 '2025-02-10',
 180.00,
 'Exemplar para consulta - Acervo Geral');

INSERT INTO exemplares (id_exemplar, id_obra, codigo_barras, estado_conservacao, disponivel, data_aquisicao, valor_aquisicao, observacoes) VALUES
(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'Mistérios do Mechanicus' LIMIT 1),
 CONCAT('978-1-84154-002-', FLOOR(RAND() * 10)),
 'Bom',
 TRUE,
 '2025-03-05',
 175.00,
 'Exemplar técnico - Acervo Especializado'),

(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'Mistérios do Mechanicus' LIMIT 1),
 CONCAT('978-1-84154-002-', FLOOR(RAND() * 10)),
 'Regular',
 TRUE,
 '2025-03-05',
 120.00,
 'Exemplar com anotações - Uso interno');

INSERT INTO exemplares (id_exemplar, id_obra, codigo_barras, estado_conservacao, disponivel, data_aquisicao, valor_aquisicao, observacoes) VALUES
(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'Xenos: Uma Ameaça Constante' LIMIT 1),
 CONCAT('978-1-84154-003-', FLOOR(RAND() * 10)),
 'Excelente',
 TRUE,
 '2025-04-20',
 190.00,
 'Catálogo ilustrado - Acervo Geral'),

(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'Xenos: Uma Ameaça Constante' LIMIT 1),
 CONCAT('978-1-84154-003-', FLOOR(RAND() * 10)),
 'Bom',
 TRUE,
 '2025-04-20',
 190.00,
 'Exemplar de consulta - Sala de Leitura'),

(CONCAT('EXE-', LPAD(FLOOR(RAND() * 999999), 6, '0')), 
 (SELECT id_obra FROM obras WHERE titulo = 'Xenos: Uma Ameaça Constante' LIMIT 1),
 CONCAT('978-1-84154-003-', FLOOR(RAND() * 10)),
 'Bom',
 TRUE,
 '2025-04-20',
 190.00,
 'Exemplar de empréstimo - Circulação');

SELECT 
    e.id_exemplar,
    o.titulo AS obra,
    e.codigo_barras,
    e.estado_conservacao,
    e.disponivel,
    e.observacoes
FROM exemplares e
INNER JOIN obras o ON e.id_obra = o.id_obra
ORDER BY o.titulo, e.data_cadastro;

SELECT 
    o.titulo AS obra,
    COUNT(e.id_exemplar) AS total_exemplares,
    SUM(CASE WHEN e.disponivel = TRUE THEN 1 ELSE 0 END) AS disponiveis,
    SUM(CASE WHEN e.disponivel = FALSE THEN 1 ELSE 0 END) AS emprestados
FROM obras o
LEFT JOIN exemplares e ON o.id_obra = e.id_obra
GROUP BY o.id_obra, o.titulo
ORDER BY o.titulo;
