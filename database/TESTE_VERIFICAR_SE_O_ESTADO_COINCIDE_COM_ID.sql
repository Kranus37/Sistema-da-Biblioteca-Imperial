SELECT id_exemplar, estado_conservacao, 
       HEX(estado_conservacao) as hex_value,
       LENGTH(estado_conservacao) as tamanho
FROM exemplares 
LIMIT 5;
