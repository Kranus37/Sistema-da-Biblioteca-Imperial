USE biblioteca_imperial;

SET SQL_SAFE_UPDATES = 0;

UPDATE exemplares SET estado_conservacao = 'BOM' WHERE estado_conservacao = 'Bom';
UPDATE exemplares SET estado_conservacao = 'EXCELENTE' WHERE estado_conservacao = 'Excelente';
UPDATE exemplares SET estado_conservacao = 'REGULAR' WHERE estado_conservacao = 'Regular';

SET SQL_SAFE_UPDATES = 1;

SELECT DISTINCT estado_conservacao FROM exemplares;
