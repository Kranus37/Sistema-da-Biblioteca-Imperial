USE biblioteca_imperial;

-- Desabilitar safe mode temporariamente
SET SQL_SAFE_UPDATES = 0;

-- Corrigir valores
UPDATE exemplares SET estado_conservacao = 'BOM' WHERE estado_conservacao = 'Bom';
UPDATE exemplares SET estado_conservacao = 'EXCELENTE' WHERE estado_conservacao = 'Excelente';
UPDATE exemplares SET estado_conservacao = 'REGULAR' WHERE estado_conservacao = 'Regular';

-- Reabilitar safe mode
SET SQL_SAFE_UPDATES = 1;

-- Verificar resultado
SELECT DISTINCT estado_conservacao FROM exemplares;

-- Resultado esperado:
-- BOM
-- EXCELENTE
-- REGULAR
-- RUIM (se houver)
-- PESSIMO (se houver)
