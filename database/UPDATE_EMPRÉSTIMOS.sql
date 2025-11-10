SET SQL_SAFE_UPDATES = 0;

UPDATE EMPRESTIMOS SET status_emprestimo = 'ATIVO' WHERE status_emprestimo = 'Ativo';
UPDATE EMPRESTIMOS SET status_emprestimo = 'DEVOLVIDO' WHERE status_emprestimo = 'Devolvido';
UPDATE EMPRESTIMOS SET status_emprestimo = 'ATRASADO' WHERE status_emprestimo = 'Atrasado';

UPDATE EXEMPLARES SET estado_conservacao = 'BOM' WHERE estado_conservacao = 'Bom';
UPDATE EXEMPLARES SET estado_conservacao = 'OTIMO' WHERE estado_conservacao = 'Ótimo' OR estado_conservacao = 'Otimo';
UPDATE EXEMPLARES SET estado_conservacao = 'REGULAR' WHERE estado_conservacao = 'Regular';
UPDATE EXEMPLARES SET estado_conservacao = 'RUIM' WHERE estado_conservacao = 'Ruim';
UPDATE EXEMPLARES SET estado_conservacao = 'PESSIMO' WHERE estado_conservacao = 'Péssimo' OR estado_conservacao = 'Pessimo';

SET SQL_SAFE_UPDATES = 1;
