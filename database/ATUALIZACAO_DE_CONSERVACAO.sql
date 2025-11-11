ALTER TABLE exemplares 
MODIFY COLUMN estado_conservacao VARCHAR(20) 
COLLATE utf8mb4_bin;

UPDATE exemplares SET estado_conservacao = 'BOM' WHERE id_exemplar = 'EXE-166980';
SELECT estado_conservacao FROM exemplares WHERE id_exemplar = 'EXE-166980';

UPDATE exemplares SET estado_conservacao = 'BOM' 
WHERE estado_conservacao IN ('Bom', 'bom', 'BOM');

UPDATE exemplares SET estado_conservacao = 'EXCELENTE' 
WHERE estado_conservacao IN ('Excelente', 'excelente', 'EXCELENTE');

UPDATE exemplares SET estado_conservacao = 'REGULAR' 
WHERE estado_conservacao IN ('Regular', 'regular', 'REGULAR');

SELECT DISTINCT estado_conservacao FROM exemplares;
