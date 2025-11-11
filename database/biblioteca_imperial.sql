-- Descrição: Script completo de criação do banco de dados relacional MySQL
--            para o sistema de gerenciamento da Biblioteca Imperial.
-- ============================================================================

-- ============================================================================
-- SEÇÃO 1: CRIAÇÃO DO BANCO DE DADOS E CONFIGURAÇÕES INICIAIS
-- ============================================================================
-- Cria o banco de dados com charset UTF-8 para suportar caracteres especiais
CREATE DATABASE biblioteca_imperial
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Seleciona o banco de dados para uso
USE biblioteca_imperial;

-- ============================================================================
-- SEÇÃO 2: CRIAÇÃO DAS TABELAS
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Tabela: grupos_usuarios
-- Descrição: Armazena os grupos de permissões para controle de acesso.
--            Representa as diferentes hierarquias dentro da Biblioteca Imperial.
-- ----------------------------------------------------------------------------
CREATE TABLE grupos_usuarios (
    id_grupo VARCHAR(20) PRIMARY KEY COMMENT 'Identificador único do grupo',
    nome_grupo VARCHAR(100) NOT NULL UNIQUE COMMENT 'Nome do grupo de usuários',
    descricao TEXT COMMENT 'Descrição das permissões e responsabilidades do grupo',
    nivel_acesso INT NOT NULL COMMENT 'Nível de acesso (1=básico, 5=administrativo)',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação do grupo',
    ativo BOOLEAN DEFAULT TRUE COMMENT 'Indica se o grupo está ativo'
) COMMENT='Grupos de usuários para controle de permissões';

-- ----------------------------------------------------------------------------
-- Tabela: usuarios
-- Descrição: Armazena informações dos usuários do sistema.
--            Representa os servos do Imperador com acesso à Biblioteca Imperial.
-- ----------------------------------------------------------------------------
CREATE TABLE usuarios (
    id_usuario VARCHAR(30) PRIMARY KEY COMMENT 'Identificador único do usuário',
    nome_completo VARCHAR(200) NOT NULL COMMENT 'Nome completo do usuário',
    email VARCHAR(150) NOT NULL UNIQUE COMMENT 'Email único do usuário',
    senha_hash VARCHAR(255) NOT NULL COMMENT 'Hash da senha do usuário',
    id_grupo VARCHAR(20) NOT NULL COMMENT 'Grupo ao qual o usuário pertence',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de cadastro do usuário',
    ultimo_acesso TIMESTAMP NULL COMMENT 'Data do último acesso ao sistema',
    ativo BOOLEAN DEFAULT TRUE COMMENT 'Indica se o usuário está ativo',
    tentativas_login INT DEFAULT 0 COMMENT 'Contador de tentativas de login falhadas',
    FOREIGN KEY (id_grupo) REFERENCES grupos_usuarios(id_grupo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) COMMENT='Usuários do sistema da Biblioteca Imperial';

-- ----------------------------------------------------------------------------
-- Tabela: categorias
-- Descrição: Categorias de obras disponíveis na Biblioteca Imperial.
--            Representa os diferentes tipos de conhecimento do Imperium.
-- ----------------------------------------------------------------------------
CREATE TABLE categorias (
    id_categoria VARCHAR(20) PRIMARY KEY COMMENT 'Identificador único da categoria',
    nome_categoria VARCHAR(100) NOT NULL UNIQUE COMMENT 'Nome da categoria',
    descricao TEXT COMMENT 'Descrição da categoria',
    nivel_restricao INT DEFAULT 0 COMMENT 'Nível de restrição de acesso (0=público, 5=restrito)',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de criação da categoria'
) COMMENT='Categorias de obras da Biblioteca Imperial';

-- ----------------------------------------------------------------------------
-- Tabela: autores
-- Descrição: Autores das obras catalogadas na Biblioteca Imperial.
-- ----------------------------------------------------------------------------
CREATE TABLE autores (
    id_autor VARCHAR(20) PRIMARY KEY COMMENT 'Identificador único do autor',
    nome_autor VARCHAR(200) NOT NULL COMMENT 'Nome do autor',
    biografia TEXT COMMENT 'Biografia do autor',
    data_nascimento DATE COMMENT 'Data de nascimento do autor',
    data_falecimento DATE COMMENT 'Data de falecimento do autor (se aplicável)',
    nacionalidade VARCHAR(100) COMMENT 'Nacionalidade do autor',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de cadastro do autor'
) COMMENT='Autores das obras da Biblioteca Imperial';

-- ----------------------------------------------------------------------------
-- Tabela: obras
-- Descrição: Obras (livros, manuscritos, etc.) catalogadas na Biblioteca Imperial.
-- ----------------------------------------------------------------------------
CREATE TABLE obras (
    id_obra VARCHAR(30) PRIMARY KEY COMMENT 'Identificador único da obra',
    titulo VARCHAR(300) NOT NULL COMMENT 'Título da obra',
    subtitulo VARCHAR(300) COMMENT 'Subtítulo da obra',
    id_categoria VARCHAR(20) NOT NULL COMMENT 'Categoria da obra',
    isbn VARCHAR(20) UNIQUE COMMENT 'ISBN da obra (se aplicável)',
    ano_publicacao INT COMMENT 'Ano de publicação da obra',
    editora VARCHAR(150) COMMENT 'Editora da obra',
    edicao VARCHAR(50) COMMENT 'Edição da obra',
    idioma VARCHAR(50) DEFAULT 'Português' COMMENT 'Idioma da obra',
    num_paginas INT COMMENT 'Número de páginas da obra',
    sinopse TEXT COMMENT 'Sinopse da obra',
    localizacao_fisica VARCHAR(100) COMMENT 'Localização física na biblioteca',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de cadastro da obra',
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Data da última atualização',
    ativo BOOLEAN DEFAULT TRUE COMMENT 'Indica se a obra está ativa no catálogo',
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) COMMENT='Obras catalogadas na Biblioteca Imperial';

-- ----------------------------------------------------------------------------
-- Tabela: obras_autores
-- Descrição: Relacionamento N:N entre obras e autores.
--            Uma obra pode ter múltiplos autores e um autor pode ter múltiplas obras.
-- ----------------------------------------------------------------------------
CREATE TABLE obras_autores (
    id_obra VARCHAR(30) NOT NULL COMMENT 'Identificador da obra',
    id_autor VARCHAR(20) NOT NULL COMMENT 'Identificador do autor',
    ordem_autoria INT DEFAULT 1 COMMENT 'Ordem do autor na obra (1=principal)',
    PRIMARY KEY (id_obra, id_autor),
    FOREIGN KEY (id_obra) REFERENCES obras(id_obra)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (id_autor) REFERENCES autores(id_autor)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) COMMENT='Relacionamento entre obras e autores';

-- ----------------------------------------------------------------------------
-- Tabela: exemplares
-- Descrição: Exemplares físicos das obras disponíveis na Biblioteca Imperial.
--            Uma obra pode ter múltiplos exemplares.
-- ----------------------------------------------------------------------------
CREATE TABLE exemplares (
    id_exemplar VARCHAR(30) PRIMARY KEY COMMENT 'Identificador único do exemplar',
    id_obra VARCHAR(30) NOT NULL COMMENT 'Obra à qual o exemplar pertence',
    codigo_barras VARCHAR(50) UNIQUE COMMENT 'Código de barras do exemplar',
    estado_conservacao ENUM('Excelente', 'Bom', 'Regular', 'Ruim', 'Péssimo') DEFAULT 'Bom' COMMENT 'Estado de conservação do exemplar',
    disponivel BOOLEAN DEFAULT TRUE COMMENT 'Indica se o exemplar está disponível para empréstimo',
    data_aquisicao DATE COMMENT 'Data de aquisição do exemplar',
    valor_aquisicao DECIMAL(10, 2) COMMENT 'Valor de aquisição do exemplar',
    observacoes TEXT COMMENT 'Observações sobre o exemplar',
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de cadastro do exemplar',
    FOREIGN KEY (id_obra) REFERENCES obras(id_obra)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) COMMENT='Exemplares físicos das obras';

-- ----------------------------------------------------------------------------
-- Tabela: emprestimos
-- Descrição: Registros de empréstimos de exemplares aos usuários.
-- ----------------------------------------------------------------------------
CREATE TABLE emprestimos (
    id_emprestimo VARCHAR(30) PRIMARY KEY COMMENT 'Identificador único do empréstimo',
    id_exemplar VARCHAR(30) NOT NULL COMMENT 'Exemplar emprestado',
    id_usuario VARCHAR(30) NOT NULL COMMENT 'Usuário que realizou o empréstimo',
    data_emprestimo TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data do empréstimo',
    data_prevista_devolucao DATE NOT NULL COMMENT 'Data prevista para devolução',
    data_devolucao TIMESTAMP NULL COMMENT 'Data efetiva de devolução',
    renovacoes INT DEFAULT 0 COMMENT 'Número de renovações realizadas',
    status_emprestimo ENUM('Ativo', 'Devolvido', 'Atrasado', 'Cancelado') DEFAULT 'Ativo' COMMENT 'Status do empréstimo',
    observacoes TEXT COMMENT 'Observações sobre o empréstimo',
    FOREIGN KEY (id_exemplar) REFERENCES exemplares(id_exemplar)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) COMMENT='Registros de empréstimos de exemplares';

-- ----------------------------------------------------------------------------
-- Tabela: reservas
-- Descrição: Reservas de obras pelos usuários quando não há exemplares disponíveis.
-- ----------------------------------------------------------------------------
CREATE TABLE reservas (
    id_reserva VARCHAR(30) PRIMARY KEY COMMENT 'Identificador único da reserva',
    id_obra VARCHAR(30) NOT NULL COMMENT 'Obra reservada',
    id_usuario VARCHAR(30) NOT NULL COMMENT 'Usuário que realizou a reserva',
    data_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data da reserva',
    status_reserva ENUM('Ativa', 'Atendida', 'Cancelada', 'Expirada') DEFAULT 'Ativa' COMMENT 'Status da reserva',
    data_expiracao DATE NOT NULL COMMENT 'Data de expiração da reserva',
    data_atendimento TIMESTAMP NULL COMMENT 'Data em que a reserva foi atendida',
    FOREIGN KEY (id_obra) REFERENCES obras(id_obra)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
) COMMENT='Reservas de obras pelos usuários';

-- ----------------------------------------------------------------------------
-- Tabela: multas
-- Descrição: Multas aplicadas aos usuários por atrasos ou danos.
-- ----------------------------------------------------------------------------
CREATE TABLE multas (
    id_multa VARCHAR(30) PRIMARY KEY COMMENT 'Identificador único da multa',
    id_emprestimo VARCHAR(30) COMMENT 'Empréstimo relacionado à multa (se aplicável)',
    id_usuario VARCHAR(30) NOT NULL COMMENT 'Usuário multado',
    tipo_multa ENUM('Atraso', 'Dano', 'Perda', 'Outros') NOT NULL COMMENT 'Tipo da multa',
    valor_multa DECIMAL(10, 2) NOT NULL COMMENT 'Valor da multa',
    data_multa TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data de aplicação da multa',
    data_pagamento TIMESTAMP NULL COMMENT 'Data de pagamento da multa',
    status_multa ENUM('Pendente', 'Paga', 'Cancelada') DEFAULT 'Pendente' COMMENT 'Status da multa',
    descricao TEXT COMMENT 'Descrição da multa',
    FOREIGN KEY (id_emprestimo) REFERENCES emprestimos(id_emprestimo)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
) COMMENT='Multas aplicadas aos usuários';

-- ----------------------------------------------------------------------------
-- Tabela: logs_sistema
-- Descrição: Logs de ações realizadas no sistema para auditoria.
-- ----------------------------------------------------------------------------
CREATE TABLE logs_sistema (
    id_log BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Identificador único do log',
    id_usuario VARCHAR(30) COMMENT 'Usuário que realizou a ação',
    acao VARCHAR(100) NOT NULL COMMENT 'Ação realizada',
    tabela_afetada VARCHAR(50) COMMENT 'Tabela afetada pela ação',
    registro_afetado VARCHAR(30) COMMENT 'ID do registro afetado',
    detalhes TEXT COMMENT 'Detalhes da ação',
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Data e hora da ação',
    ip_origem VARCHAR(45) COMMENT 'IP de origem da ação',
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE SET NULL
) COMMENT='Logs de ações do sistema para auditoria';

-- Justificativa para AUTO_INCREMENT em logs_sistema:
-- A tabela de logs pode crescer exponencialmente e requer inserções extremamente rápidas.
-- O uso de AUTO_INCREMENT é justificado para garantir performance e evitar overhead
-- de geração manual de IDs em operações de alta frequência.

-- ============================================================================
-- SEÇÃO 3: CRIAÇÃO DE ÍNDICES
-- ============================================================================

-- Índices para otimização de consultas frequentes

-- Índice na tabela usuarios para busca por email (login)
CREATE INDEX idx_usuarios_email ON usuarios(email);
-- Justificativa: O email é usado como identificador de login, sendo consultado
-- frequentemente durante a autenticação.

-- Índice na tabela usuarios para busca por grupo
CREATE INDEX idx_usuarios_grupo ON usuarios(id_grupo);
-- Justificativa: Consultas frequentes filtram usuários por grupo para controle
-- de permissões e relatórios.

-- Índice na tabela obras para busca por categoria
CREATE INDEX idx_obras_categoria ON obras(id_categoria);
-- Justificativa: Usuários frequentemente navegam e filtram obras por categoria.

-- Índice na tabela obras para busca por título
CREATE INDEX idx_obras_titulo ON obras(titulo);
-- Justificativa: A busca por título é uma das operações mais comuns no sistema.

-- Índice na tabela emprestimos para busca por usuário
CREATE INDEX idx_emprestimos_usuario ON emprestimos(id_usuario);
-- Justificativa: Consultas frequentes para listar empréstimos de um usuário específico.

-- Índice na tabela emprestimos para busca por status
CREATE INDEX idx_emprestimos_status ON emprestimos(status_emprestimo);
-- Justificativa: Relatórios e consultas frequentes filtram empréstimos por status
-- (ativos, atrasados, etc.).

-- Índice composto na tabela emprestimos para busca por data de devolução prevista
CREATE INDEX idx_emprestimos_devolucao ON emprestimos(data_prevista_devolucao, status_emprestimo);
-- Justificativa: Usado para identificar empréstimos próximos do vencimento ou atrasados,
-- combinando data e status para maior eficiência.

-- Índice na tabela exemplares para busca por obra
CREATE INDEX idx_exemplares_obra ON exemplares(id_obra);
-- Justificativa: Consultas frequentes para listar todos os exemplares de uma obra.

-- Índice na tabela reservas para busca por usuário
CREATE INDEX idx_reservas_usuario ON reservas(id_usuario);
-- Justificativa: Consultas frequentes para listar reservas de um usuário específico.

-- Índice na tabela reservas para busca por status
CREATE INDEX idx_reservas_status ON reservas(status_reserva);
-- Justificativa: Relatórios e processamento de reservas ativas.

-- Índice na tabela multas para busca por usuário
CREATE INDEX idx_multas_usuario ON multas(id_usuario);
-- Justificativa: Consultas frequentes para listar multas pendentes de um usuário.

-- Índice na tabela multas para busca por status
CREATE INDEX idx_multas_status ON multas(status_multa);
-- Justificativa: Relatórios financeiros e consultas de multas pendentes.

-- ============================================================================
-- SEÇÃO 4: CRIAÇÃO DE FUNÇÕES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Função: gerar_id_grupo
-- Descrição: Gera um ID único para grupos de usuários no formato GRP-XXXXX
-- Retorno: VARCHAR(20) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_grupo()
RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(20);
    DECLARE contador INT;
    
    -- Obtém o número de grupos existentes
    SELECT COUNT(*) INTO contador FROM grupos_usuarios;
    
    -- Gera o ID no formato GRP-XXXXX
    SET novo_id = CONCAT('GRP-', LPAD(contador + 1, 5, '0'));
    
    -- Verifica se o ID já existe (prevenção de duplicatas)
    WHILE EXISTS (SELECT 1 FROM grupos_usuarios WHERE id_grupo = novo_id) DO
        SET contador = contador + 1;
        SET novo_id = CONCAT('GRP-', LPAD(contador + 1, 5, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: gerar_id_usuario
-- Descrição: Gera um ID único para usuários no formato USR-XXXXXXXXXX
--            (baseado em timestamp para garantir unicidade)
-- Retorno: VARCHAR(30) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_usuario()
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(30);
    DECLARE timestamp_str VARCHAR(20);
    
    -- Gera um timestamp único
    SET timestamp_str = UNIX_TIMESTAMP();
    
    -- Gera o ID no formato USR-XXXXXXXXXX
    SET novo_id = CONCAT('USR-', timestamp_str, LPAD(FLOOR(RAND() * 1000), 3, '0'));
    
    -- Verifica se o ID já existe (prevenção de duplicatas)
    WHILE EXISTS (SELECT 1 FROM usuarios WHERE id_usuario = novo_id) DO
        SET novo_id = CONCAT('USR-', UNIX_TIMESTAMP(), LPAD(FLOOR(RAND() * 1000), 3, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: gerar_id_categoria
-- Descrição: Gera um ID único para categorias no formato CAT-XXXXX
-- Retorno: VARCHAR(20) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_categoria()
RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(20);
    DECLARE contador INT;
    
    SELECT COUNT(*) INTO contador FROM categorias;
    SET novo_id = CONCAT('CAT-', LPAD(contador + 1, 5, '0'));
    
    WHILE EXISTS (SELECT 1 FROM categorias WHERE id_categoria = novo_id) DO
        SET contador = contador + 1;
        SET novo_id = CONCAT('CAT-', LPAD(contador + 1, 5, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: gerar_id_autor
-- Descrição: Gera um ID único para autores no formato AUT-XXXXX
-- Retorno: VARCHAR(20) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_autor()
RETURNS VARCHAR(20)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(20);
    DECLARE contador INT;
    
    SELECT COUNT(*) INTO contador FROM autores;
    SET novo_id = CONCAT('AUT-', LPAD(contador + 1, 5, '0'));
    
    WHILE EXISTS (SELECT 1 FROM autores WHERE id_autor = novo_id) DO
        SET contador = contador + 1;
        SET novo_id = CONCAT('AUT-', LPAD(contador + 1, 5, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: gerar_id_obra
-- Descrição: Gera um ID único para obras no formato OBR-XXXXXXXXXX
-- Retorno: VARCHAR(30) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_obra()
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(30);
    DECLARE timestamp_str VARCHAR(20);
    
    SET timestamp_str = UNIX_TIMESTAMP();
    SET novo_id = CONCAT('OBR-', timestamp_str, LPAD(FLOOR(RAND() * 1000), 3, '0'));
    
    WHILE EXISTS (SELECT 1 FROM obras WHERE id_obra = novo_id) DO
        SET novo_id = CONCAT('OBR-', UNIX_TIMESTAMP(), LPAD(FLOOR(RAND() * 1000), 3, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: gerar_id_exemplar
-- Descrição: Gera um ID único para exemplares no formato EXE-XXXXXXXXXX
-- Retorno: VARCHAR(30) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_exemplar()
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(30);
    DECLARE timestamp_str VARCHAR(20);
    
    SET timestamp_str = UNIX_TIMESTAMP();
    SET novo_id = CONCAT('EXE-', timestamp_str, LPAD(FLOOR(RAND() * 1000), 3, '0'));
    
    WHILE EXISTS (SELECT 1 FROM exemplares WHERE id_exemplar = novo_id) DO
        SET novo_id = CONCAT('EXE-', UNIX_TIMESTAMP(), LPAD(FLOOR(RAND() * 1000), 3, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: gerar_id_emprestimo
-- Descrição: Gera um ID único para empréstimos no formato EMP-XXXXXXXXXX
-- Retorno: VARCHAR(30) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_emprestimo()
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(30);
    DECLARE timestamp_str VARCHAR(20);
    
    SET timestamp_str = UNIX_TIMESTAMP();
    SET novo_id = CONCAT('EMP-', timestamp_str, LPAD(FLOOR(RAND() * 1000), 3, '0'));
    
    WHILE EXISTS (SELECT 1 FROM emprestimos WHERE id_emprestimo = novo_id) DO
        SET novo_id = CONCAT('EMP-', UNIX_TIMESTAMP(), LPAD(FLOOR(RAND() * 1000), 3, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: gerar_id_reserva
-- Descrição: Gera um ID único para reservas no formato RES-XXXXXXXXXX
-- Retorno: VARCHAR(30) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_reserva()
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(30);
    DECLARE timestamp_str VARCHAR(20);
    
    SET timestamp_str = UNIX_TIMESTAMP();
    SET novo_id = CONCAT('RES-', timestamp_str, LPAD(FLOOR(RAND() * 1000), 3, '0'));
    
    WHILE EXISTS (SELECT 1 FROM reservas WHERE id_reserva = novo_id) DO
        SET novo_id = CONCAT('RES-', UNIX_TIMESTAMP(), LPAD(FLOOR(RAND() * 1000), 3, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: gerar_id_multa
-- Descrição: Gera um ID único para multas no formato MLT-XXXXXXXXXX
-- Retorno: VARCHAR(30) - ID gerado
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION gerar_id_multa()
RETURNS VARCHAR(30)
DETERMINISTIC
BEGIN
    DECLARE novo_id VARCHAR(30);
    DECLARE timestamp_str VARCHAR(20);
    
    SET timestamp_str = UNIX_TIMESTAMP();
    SET novo_id = CONCAT('MLT-', timestamp_str, LPAD(FLOOR(RAND() * 1000), 3, '0'));
    
    WHILE EXISTS (SELECT 1 FROM multas WHERE id_multa = novo_id) DO
        SET novo_id = CONCAT('MLT-', UNIX_TIMESTAMP(), LPAD(FLOOR(RAND() * 1000), 3, '0'));
    END WHILE;
    
    RETURN novo_id;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: calcular_dias_atraso
-- Descrição: Calcula o número de dias de atraso de um empréstimo
-- Parâmetros: p_data_prevista (DATE) - Data prevista de devolução
-- Retorno: INT - Número de dias de atraso (0 se não houver atraso)
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION calcular_dias_atraso(p_data_prevista DATE)
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE dias_atraso INT;
    
    -- Calcula a diferença entre a data atual e a data prevista
    SET dias_atraso = DATEDIFF(CURDATE(), p_data_prevista);
    
    -- Retorna 0 se não houver atraso
    IF dias_atraso < 0 THEN
        SET dias_atraso = 0;
    END IF;
    
    RETURN dias_atraso;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Função: calcular_valor_multa_atraso
-- Descrição: Calcula o valor da multa por atraso (R$ 2,00 por dia)
-- Parâmetros: p_dias_atraso (INT) - Número de dias de atraso
-- Retorno: DECIMAL(10,2) - Valor da multa
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE FUNCTION calcular_valor_multa_atraso(p_dias_atraso INT)
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE valor_multa DECIMAL(10,2);
    DECLARE valor_por_dia DECIMAL(10,2) DEFAULT 2.00;
    
    -- Calcula o valor da multa (R$ 2,00 por dia de atraso)
    SET valor_multa = p_dias_atraso * valor_por_dia;
    
    RETURN valor_multa;
END//
DELIMITER ;

-- ============================================================================
-- SEÇÃO 5: CRIAÇÃO DE PROCEDURES
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Procedure: realizar_emprestimo
-- Descrição: Realiza o empréstimo de um exemplar para um usuário
-- Parâmetros:
--   - p_id_exemplar: ID do exemplar a ser emprestado
--   - p_id_usuario: ID do usuário que realizará o empréstimo
--   - p_dias_emprestimo: Número de dias do empréstimo (padrão: 14)
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE PROCEDURE realizar_emprestimo(
    IN p_id_exemplar VARCHAR(30),
    IN p_id_usuario VARCHAR(30),
    IN p_dias_emprestimo INT
)
BEGIN
    DECLARE v_id_emprestimo VARCHAR(30);
    DECLARE v_disponivel BOOLEAN;
    DECLARE v_usuario_ativo BOOLEAN;
    DECLARE v_multas_pendentes INT;
    
    -- Verifica se o exemplar está disponível
    SELECT disponivel INTO v_disponivel
    FROM exemplares
    WHERE id_exemplar = p_id_exemplar;
    
    -- Verifica se o usuário está ativo
    SELECT ativo INTO v_usuario_ativo
    FROM usuarios
    WHERE id_usuario = p_id_usuario;
    
    -- Verifica se o usuário possui multas pendentes
    SELECT COUNT(*) INTO v_multas_pendentes
    FROM multas
    WHERE id_usuario = p_id_usuario AND status_multa = 'Pendente';
    
    -- Validações
    IF v_disponivel IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Exemplar não encontrado';
    ELSEIF v_disponivel = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Exemplar não disponível para empréstimo';
    ELSEIF v_usuario_ativo IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Usuário não encontrado';
    ELSEIF v_usuario_ativo = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Usuário inativo';
    ELSEIF v_multas_pendentes > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Usuário possui multas pendentes';
    END IF;
    
    -- Gera o ID do empréstimo
    SET v_id_emprestimo = gerar_id_emprestimo();
    
    -- Insere o empréstimo
    INSERT INTO emprestimos (
        id_emprestimo,
        id_exemplar,
        id_usuario,
        data_emprestimo,
        data_prevista_devolucao,
        status_emprestimo
    ) VALUES (
        v_id_emprestimo,
        p_id_exemplar,
        p_id_usuario,
        NOW(),
        DATE_ADD(CURDATE(), INTERVAL p_dias_emprestimo DAY),
        'Ativo'
    );
    
    -- Atualiza a disponibilidade do exemplar
    UPDATE exemplares
    SET disponivel = FALSE
    WHERE id_exemplar = p_id_exemplar;
    
    -- Registra no log
    INSERT INTO logs_sistema (id_usuario, acao, tabela_afetada, registro_afetado, detalhes)
    VALUES (p_id_usuario, 'EMPRESTIMO_REALIZADO', 'emprestimos', v_id_emprestimo, 
            CONCAT('Empréstimo do exemplar ', p_id_exemplar));
    
    -- Retorna o ID do empréstimo
    SELECT v_id_emprestimo AS id_emprestimo, 'Empréstimo realizado com sucesso' AS mensagem;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Procedure: realizar_devolucao
-- Descrição: Realiza a devolução de um exemplar emprestado
-- Parâmetros:
--   - p_id_emprestimo: ID do empréstimo a ser devolvido
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE PROCEDURE realizar_devolucao(
    IN p_id_emprestimo VARCHAR(30)
)
BEGIN
    DECLARE v_id_exemplar VARCHAR(30);
    DECLARE v_id_usuario VARCHAR(30);
    DECLARE v_data_prevista DATE;
    DECLARE v_dias_atraso INT;
    DECLARE v_valor_multa DECIMAL(10,2);
    DECLARE v_id_multa VARCHAR(30);
    DECLARE v_status_atual VARCHAR(20);
    
    -- Obtém informações do empréstimo
    SELECT id_exemplar, id_usuario, data_prevista_devolucao, status_emprestimo
    INTO v_id_exemplar, v_id_usuario, v_data_prevista, v_status_atual
    FROM emprestimos
    WHERE id_emprestimo = p_id_emprestimo;
    
    -- Validações
    IF v_id_exemplar IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Empréstimo não encontrado';
    ELSEIF v_status_atual = 'Devolvido' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Empréstimo já foi devolvido';
    ELSEIF v_status_atual = 'Cancelado' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Empréstimo foi cancelado';
    END IF;
    
    -- Calcula dias de atraso
    SET v_dias_atraso = calcular_dias_atraso(v_data_prevista);
    
    -- Se houver atraso, gera multa
    IF v_dias_atraso > 0 THEN
        SET v_valor_multa = calcular_valor_multa_atraso(v_dias_atraso);
        SET v_id_multa = gerar_id_multa();
        
        INSERT INTO multas (
            id_multa,
            id_emprestimo,
            id_usuario,
            tipo_multa,
            valor_multa,
            status_multa,
            descricao
        ) VALUES (
            v_id_multa,
            p_id_emprestimo,
            v_id_usuario,
            'Atraso',
            v_valor_multa,
            'Pendente',
            CONCAT('Multa por atraso de ', v_dias_atraso, ' dia(s)')
        );
    END IF;
    
    -- Atualiza o empréstimo
    UPDATE emprestimos
    SET data_devolucao = NOW(),
        status_emprestimo = 'Devolvido'
    WHERE id_emprestimo = p_id_emprestimo;
    
    -- Libera o exemplar
    UPDATE exemplares
    SET disponivel = TRUE
    WHERE id_exemplar = v_id_exemplar;
    
    -- Registra no log
    INSERT INTO logs_sistema (id_usuario, acao, tabela_afetada, registro_afetado, detalhes)
    VALUES (v_id_usuario, 'DEVOLUCAO_REALIZADA', 'emprestimos', p_id_emprestimo,
            CONCAT('Devolução do exemplar ', v_id_exemplar, '. Dias de atraso: ', v_dias_atraso));
    
    -- Retorna informações da devolução
    SELECT 
        'Devolução realizada com sucesso' AS mensagem,
        v_dias_atraso AS dias_atraso,
        v_valor_multa AS valor_multa;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Procedure: renovar_emprestimo
-- Descrição: Renova um empréstimo ativo, estendendo o prazo de devolução
-- Parâmetros:
--   - p_id_emprestimo: ID do empréstimo a ser renovado
--   - p_dias_renovacao: Número de dias adicionais (padrão: 7)
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE PROCEDURE renovar_emprestimo(
    IN p_id_emprestimo VARCHAR(30),
    IN p_dias_renovacao INT
)
BEGIN
    DECLARE v_status_atual VARCHAR(20);
    DECLARE v_renovacoes_atuais INT;
    DECLARE v_id_usuario VARCHAR(30);
    DECLARE v_max_renovacoes INT DEFAULT 3;
    
    -- Obtém informações do empréstimo
    SELECT status_emprestimo, renovacoes, id_usuario
    INTO v_status_atual, v_renovacoes_atuais, v_id_usuario
    FROM emprestimos
    WHERE id_emprestimo = p_id_emprestimo;
    
    -- Validações
    IF v_status_atual IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Empréstimo não encontrado';
    ELSEIF v_status_atual != 'Ativo' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Apenas empréstimos ativos podem ser renovados';
    ELSEIF v_renovacoes_atuais >= v_max_renovacoes THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Número máximo de renovações atingido';
    END IF;
    
    -- Atualiza o empréstimo
    UPDATE emprestimos
    SET data_prevista_devolucao = DATE_ADD(data_prevista_devolucao, INTERVAL p_dias_renovacao DAY),
        renovacoes = renovacoes + 1
    WHERE id_emprestimo = p_id_emprestimo;
    
    -- Registra no log
    INSERT INTO logs_sistema (id_usuario, acao, tabela_afetada, registro_afetado, detalhes)
    VALUES (v_id_usuario, 'EMPRESTIMO_RENOVADO', 'emprestimos', p_id_emprestimo,
            CONCAT('Renovação de ', p_dias_renovacao, ' dias'));
    
    SELECT 'Empréstimo renovado com sucesso' AS mensagem;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Procedure: criar_reserva
-- Descrição: Cria uma reserva de obra para um usuário
-- Parâmetros:
--   - p_id_obra: ID da obra a ser reservada
--   - p_id_usuario: ID do usuário que realizará a reserva
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE PROCEDURE criar_reserva(
    IN p_id_obra VARCHAR(30),
    IN p_id_usuario VARCHAR(30)
)
BEGIN
    DECLARE v_id_reserva VARCHAR(30);
    DECLARE v_exemplares_disponiveis INT;
    DECLARE v_usuario_ativo BOOLEAN;
    
    -- Verifica se há exemplares disponíveis
    SELECT COUNT(*) INTO v_exemplares_disponiveis
    FROM exemplares
    WHERE id_obra = p_id_obra AND disponivel = TRUE;
    
    -- Verifica se o usuário está ativo
    SELECT ativo INTO v_usuario_ativo
    FROM usuarios
    WHERE id_usuario = p_id_usuario;
    
    -- Validações
    IF v_usuario_ativo IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Usuário não encontrado';
    ELSEIF v_usuario_ativo = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Usuário inativo';
    ELSEIF v_exemplares_disponiveis > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Há exemplares disponíveis. Realize o empréstimo diretamente.';
    END IF;
    
    -- Gera o ID da reserva
    SET v_id_reserva = gerar_id_reserva();
    
    -- Insere a reserva
    INSERT INTO reservas (
        id_reserva,
        id_obra,
        id_usuario,
        data_reserva,
        status_reserva,
        data_expiracao
    ) VALUES (
        v_id_reserva,
        p_id_obra,
        p_id_usuario,
        NOW(),
        'Ativa',
        DATE_ADD(CURDATE(), INTERVAL 7 DAY)
    );
    
    -- Registra no log
    INSERT INTO logs_sistema (id_usuario, acao, tabela_afetada, registro_afetado, detalhes)
    VALUES (p_id_usuario, 'RESERVA_CRIADA', 'reservas', v_id_reserva,
            CONCAT('Reserva da obra ', p_id_obra));
    
    SELECT v_id_reserva AS id_reserva, 'Reserva criada com sucesso' AS mensagem;
END//
DELIMITER ;

-- ============================================================================
-- SEÇÃO 6: CRIAÇÃO DE TRIGGERS
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Trigger: trg_atualizar_status_emprestimo_atrasado
-- Descrição: Atualiza automaticamente o status de empréstimos para 'Atrasado'
--            quando a data prevista de devolução é ultrapassada.
-- Tipo: BEFORE UPDATE
-- Tabela: emprestimos
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE TRIGGER trg_atualizar_status_emprestimo_atrasado
BEFORE UPDATE ON emprestimos
FOR EACH ROW
BEGIN
    -- Se o empréstimo está ativo e a data prevista já passou, marca como atrasado
    IF NEW.status_emprestimo = 'Ativo' 
       AND NEW.data_devolucao IS NULL 
       AND NEW.data_prevista_devolucao < CURDATE() THEN
        SET NEW.status_emprestimo = 'Atrasado';
    END IF;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Trigger: trg_registrar_log_usuario
-- Descrição: Registra automaticamente no log do sistema quando um usuário
--            é inserido, atualizado ou excluído.
-- Tipo: AFTER INSERT, AFTER UPDATE, AFTER DELETE
-- Tabela: usuarios
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE TRIGGER trg_registrar_log_usuario_insert
AFTER INSERT ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO logs_sistema (id_usuario, acao, tabela_afetada, registro_afetado, detalhes)
    VALUES (NEW.id_usuario, 'USUARIO_CRIADO', 'usuarios', NEW.id_usuario,
            CONCAT('Usuário criado: ', NEW.nome_completo, ' (', NEW.email, ')'));
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_registrar_log_usuario_update
AFTER UPDATE ON usuarios
FOR EACH ROW
BEGIN
    DECLARE v_detalhes TEXT;
    
    SET v_detalhes = 'Usuário atualizado: ';
    
    IF OLD.nome_completo != NEW.nome_completo THEN
        SET v_detalhes = CONCAT(v_detalhes, 'Nome: ', OLD.nome_completo, ' -> ', NEW.nome_completo, '; ');
    END IF;
    
    IF OLD.email != NEW.email THEN
        SET v_detalhes = CONCAT(v_detalhes, 'Email: ', OLD.email, ' -> ', NEW.email, '; ');
    END IF;
    
    IF OLD.ativo != NEW.ativo THEN
        SET v_detalhes = CONCAT(v_detalhes, 'Status: ', IF(OLD.ativo, 'Ativo', 'Inativo'), 
                                ' -> ', IF(NEW.ativo, 'Ativo', 'Inativo'), '; ');
    END IF;
    
    INSERT INTO logs_sistema (id_usuario, acao, tabela_afetada, registro_afetado, detalhes)
    VALUES (NEW.id_usuario, 'USUARIO_ATUALIZADO', 'usuarios', NEW.id_usuario, v_detalhes);
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER trg_registrar_log_usuario_delete
AFTER DELETE ON usuarios
FOR EACH ROW
BEGIN
    INSERT INTO logs_sistema (id_usuario, acao, tabela_afetada, registro_afetado, detalhes)
    VALUES (NULL, 'USUARIO_EXCLUIDO', 'usuarios', OLD.id_usuario,
            CONCAT('Usuário excluído: ', OLD.nome_completo, ' (', OLD.email, ')'));
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Trigger: trg_atualizar_ultimo_acesso
-- Descrição: Atualiza automaticamente o campo ultimo_acesso quando um usuário
--            realiza login (simulado por uma atualização específica).
-- Tipo: BEFORE UPDATE
-- Tabela: usuarios
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE TRIGGER trg_atualizar_ultimo_acesso
BEFORE UPDATE ON usuarios
FOR EACH ROW
BEGIN
    -- Se as tentativas de login foram zeradas, significa que houve login bem-sucedido
    IF OLD.tentativas_login > 0 AND NEW.tentativas_login = 0 THEN
        SET NEW.ultimo_acesso = NOW();
    END IF;
END//
DELIMITER ;

-- ----------------------------------------------------------------------------
-- Trigger: trg_validar_data_devolucao
-- Descrição: Valida se a data de devolução não é anterior à data do empréstimo.
-- Tipo: BEFORE UPDATE
-- Tabela: emprestimos
-- ----------------------------------------------------------------------------
DELIMITER //
CREATE TRIGGER trg_validar_data_devolucao
BEFORE UPDATE ON emprestimos
FOR EACH ROW
BEGIN
    IF NEW.data_devolucao IS NOT NULL AND NEW.data_devolucao < NEW.data_emprestimo THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Data de devolução não pode ser anterior à data do empréstimo';
    END IF;
END//
DELIMITER ;

-- ============================================================================
-- SEÇÃO 7: CRIAÇÃO DE VIEWS
-- ============================================================================

-- ----------------------------------------------------------------------------
-- View: vw_emprestimos_ativos
-- Descrição: Visualização de todos os empréstimos ativos com informações
--            detalhadas do usuário, obra e exemplar.
-- Justificativa: Facilita consultas frequentes sobre empréstimos em andamento,
--                evitando JOINs complexos repetitivos no código da aplicação.
-- ----------------------------------------------------------------------------
CREATE VIEW vw_emprestimos_ativos AS
SELECT 
    e.id_emprestimo,
    e.data_emprestimo,
    e.data_prevista_devolucao,
    e.renovacoes,
    e.status_emprestimo,
    calcular_dias_atraso(e.data_prevista_devolucao) AS dias_atraso,
    u.id_usuario,
    u.nome_completo AS nome_usuario,
    u.email AS email_usuario,
    ex.id_exemplar,
    ex.codigo_barras,
    o.id_obra,
    o.titulo AS titulo_obra,
    o.isbn,
    c.nome_categoria
FROM emprestimos e
INNER JOIN usuarios u ON e.id_usuario = u.id_usuario
INNER JOIN exemplares ex ON e.id_exemplar = ex.id_exemplar
INNER JOIN obras o ON ex.id_obra = o.id_obra
INNER JOIN categorias c ON o.id_categoria = c.id_categoria
WHERE e.status_emprestimo IN ('Ativo', 'Atrasado');

-- ----------------------------------------------------------------------------
-- View: vw_obras_disponiveis
-- Descrição: Visualização de todas as obras que possuem pelo menos um
--            exemplar disponível para empréstimo.
-- Justificativa: Otimiza a busca por obras disponíveis, essencial para a
--                funcionalidade de pesquisa e empréstimo do sistema.
-- ----------------------------------------------------------------------------
CREATE VIEW vw_obras_disponiveis AS
SELECT 
    o.id_obra,
    o.titulo,
    o.subtitulo,
    o.isbn,
    o.ano_publicacao,
    o.editora,
    o.idioma,
    o.sinopse,
    c.nome_categoria,
    c.nivel_restricao,
    COUNT(ex.id_exemplar) AS total_exemplares,
    SUM(CASE WHEN ex.disponivel = TRUE THEN 1 ELSE 0 END) AS exemplares_disponiveis,
    GROUP_CONCAT(a.nome_autor SEPARATOR ', ') AS autores
FROM obras o
INNER JOIN categorias c ON o.id_categoria = c.id_categoria
LEFT JOIN exemplares ex ON o.id_obra = ex.id_obra
LEFT JOIN obras_autores oa ON o.id_obra = oa.id_obra
LEFT JOIN autores a ON oa.id_autor = a.id_autor
WHERE o.ativo = TRUE
GROUP BY o.id_obra, o.titulo, o.subtitulo, o.isbn, o.ano_publicacao, 
         o.editora, o.idioma, o.sinopse, c.nome_categoria, c.nivel_restricao
HAVING exemplares_disponiveis > 0;

-- ----------------------------------------------------------------------------
-- View: vw_usuarios_com_pendencias
-- Descrição: Visualização de usuários que possuem empréstimos atrasados
--            ou multas pendentes.
-- Justificativa: Facilita o controle administrativo e a identificação rápida
--                de usuários com pendências financeiras ou de devolução.
-- ----------------------------------------------------------------------------
CREATE VIEW vw_usuarios_com_pendencias AS
SELECT 
    u.id_usuario,
    u.nome_completo,
    u.email,
    g.nome_grupo,
    COUNT(DISTINCT e.id_emprestimo) AS emprestimos_atrasados,
    COUNT(DISTINCT m.id_multa) AS multas_pendentes,
    COALESCE(SUM(m.valor_multa), 0) AS valor_total_multas
FROM usuarios u
INNER JOIN grupos_usuarios g ON u.id_grupo = g.id_grupo
LEFT JOIN emprestimos e ON u.id_usuario = e.id_usuario 
    AND e.status_emprestimo = 'Atrasado'
LEFT JOIN multas m ON u.id_usuario = m.id_usuario 
    AND m.status_multa = 'Pendente'
WHERE u.ativo = TRUE
GROUP BY u.id_usuario, u.nome_completo, u.email, g.nome_grupo
HAVING emprestimos_atrasados > 0 OR multas_pendentes > 0;

-- ----------------------------------------------------------------------------
-- View: vw_estatisticas_obras
-- Descrição: Estatísticas de empréstimos por obra, mostrando as obras
--            mais populares.
-- Justificativa: Fornece dados analíticos para relatórios gerenciais e
--                auxilia na tomada de decisões sobre aquisição de novos exemplares.
-- ----------------------------------------------------------------------------
CREATE VIEW vw_estatisticas_obras AS
SELECT 
    o.id_obra,
    o.titulo,
    c.nome_categoria,
    COUNT(DISTINCT ex.id_exemplar) AS total_exemplares,
    COUNT(e.id_emprestimo) AS total_emprestimos,
    COUNT(CASE WHEN e.status_emprestimo = 'Ativo' THEN 1 END) AS emprestimos_ativos,
    COUNT(CASE WHEN e.status_emprestimo = 'Devolvido' THEN 1 END) AS emprestimos_concluidos,
    GROUP_CONCAT(DISTINCT a.nome_autor SEPARATOR ', ') AS autores
FROM obras o
INNER JOIN categorias c ON o.id_categoria = c.id_categoria
LEFT JOIN exemplares ex ON o.id_obra = ex.id_obra
LEFT JOIN emprestimos e ON ex.id_exemplar = e.id_exemplar
LEFT JOIN obras_autores oa ON o.id_obra = oa.id_obra
LEFT JOIN autores a ON oa.id_autor = a.id_autor
WHERE o.ativo = TRUE
GROUP BY o.id_obra, o.titulo, c.nome_categoria
ORDER BY total_emprestimos DESC;

-- ============================================================================
-- SEÇÃO 8: CRIAÇÃO DE USUÁRIOS E CONTROLE DE ACESSO
-- ============================================================================

-- Justificativa para Usuários e Controle de Acesso:
-- O sistema implementa diferentes níveis de acesso baseados em grupos de usuários,
-- seguindo o princípio do menor privilégio. Cada usuário do banco de dados possui
-- apenas as permissões necessárias para executar suas funções, aumentando a
-- segurança e rastreabilidade das operações.

-- ----------------------------------------------------------------------------
-- Usuário: admin_biblioteca
-- Descrição: Administrador com acesso total ao banco de dados
-- Nível de Acesso: 5 (Administrativo)
-- Permissões: Todas (SELECT, INSERT, UPDATE, DELETE, EXECUTE)
-- ----------------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'admin_biblioteca'@'%' IDENTIFIED BY 'Admin@BiblioImp2024!';
GRANT ALL PRIVILEGES ON biblioteca_imperial.* TO 'admin_biblioteca'@'%';

-- ----------------------------------------------------------------------------
-- Usuário: bibliotecario
-- Descrição: Bibliotecário com acesso para gerenciar empréstimos e obras
-- Nível de Acesso: 4 (Operacional Avançado)
-- Permissões: SELECT, INSERT, UPDATE em todas as tabelas principais
--             EXECUTE em procedures e functions
-- ----------------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'bibliotecario'@'%' IDENTIFIED BY 'Biblio@2024!';
GRANT SELECT, INSERT, UPDATE ON biblioteca_imperial.* TO 'bibliotecario'@'%';
GRANT EXECUTE ON biblioteca_imperial.* TO 'bibliotecario'@'%';

-- Remove permissão de DELETE para evitar exclusões acidentais
REVOKE DELETE ON biblioteca_imperial.* FROM 'bibliotecario'@'%';

-- ----------------------------------------------------------------------------
-- Usuário: atendente
-- Descrição: Atendente com acesso para consultas e operações básicas
-- Nível de Acesso: 3 (Operacional)
-- Permissões: SELECT em todas as tabelas
--             INSERT e UPDATE em emprestimos, reservas
--             EXECUTE em procedures específicas
-- ----------------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'atendente'@'%' IDENTIFIED BY 'Atend@2024!';

-- Permissões de leitura
GRANT SELECT ON biblioteca_imperial.* TO 'atendente'@'%';

-- Permissões de escrita limitadas
GRANT INSERT, UPDATE ON biblioteca_imperial.emprestimos TO 'atendente'@'%';
GRANT INSERT, UPDATE ON biblioteca_imperial.reservas TO 'atendente'@'%';
GRANT INSERT ON biblioteca_imperial.logs_sistema TO 'atendente'@'%';

-- Permissões para executar procedures específicas
GRANT EXECUTE ON PROCEDURE biblioteca_imperial.realizar_emprestimo TO 'atendente'@'%';
GRANT EXECUTE ON PROCEDURE biblioteca_imperial.realizar_devolucao TO 'atendente'@'%';
GRANT EXECUTE ON PROCEDURE biblioteca_imperial.renovar_emprestimo TO 'atendente'@'%';
GRANT EXECUTE ON PROCEDURE biblioteca_imperial.criar_reserva TO 'atendente'@'%';

-- Permissões para executar functions necessárias
GRANT EXECUTE ON FUNCTION biblioteca_imperial.calcular_dias_atraso TO 'atendente'@'%';
GRANT EXECUTE ON FUNCTION biblioteca_imperial.calcular_valor_multa_atraso TO 'atendente'@'%';

-- ----------------------------------------------------------------------------
-- Usuário: app_backend
-- Descrição: Usuário para conexão da aplicação backend
-- Nível de Acesso: 3 (Aplicação)
-- Permissões: SELECT, INSERT, UPDATE em tabelas específicas
--             EXECUTE em procedures e functions
-- ----------------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'app_backend'@'%' IDENTIFIED BY 'AppBack@2024!';

-- Permissões de leitura
GRANT SELECT ON biblioteca_imperial.* TO 'app_backend'@'%';

-- Permissões de escrita
GRANT INSERT, UPDATE ON biblioteca_imperial.usuarios TO 'app_backend'@'%';
GRANT INSERT, UPDATE ON biblioteca_imperial.emprestimos TO 'app_backend'@'%';
GRANT INSERT, UPDATE ON biblioteca_imperial.reservas TO 'app_backend'@'%';
GRANT INSERT, UPDATE ON biblioteca_imperial.multas TO 'app_backend'@'%';
GRANT INSERT ON biblioteca_imperial.logs_sistema TO 'app_backend'@'%';

-- Permissões para executar procedures e functions
GRANT EXECUTE ON biblioteca_imperial.* TO 'app_backend'@'%';

-- ----------------------------------------------------------------------------
-- Usuário: consulta_publica
-- Descrição: Usuário para consultas públicas (catálogo online)
-- Nível de Acesso: 1 (Consulta)
-- Permissões: SELECT apenas em views e tabelas públicas
-- ----------------------------------------------------------------------------
CREATE USER IF NOT EXISTS 'consulta_publica'@'%' IDENTIFIED BY 'Consulta@2024!';

-- Permissões apenas de leitura em views públicas
GRANT SELECT ON biblioteca_imperial.vw_obras_disponiveis TO 'consulta_publica'@'%';
GRANT SELECT ON biblioteca_imperial.vw_estatisticas_obras TO 'consulta_publica'@'%';

-- Permissões de leitura em tabelas públicas
GRANT SELECT ON biblioteca_imperial.obras TO 'consulta_publica'@'%';
GRANT SELECT ON biblioteca_imperial.autores TO 'consulta_publica'@'%';
GRANT SELECT ON biblioteca_imperial.categorias TO 'consulta_publica'@'%';
GRANT SELECT ON biblioteca_imperial.obras_autores TO 'consulta_publica'@'%';

-- Aplica as permissões
FLUSH PRIVILEGES;

-- ============================================================================
-- SEÇÃO 9: INSERÇÃO DE DADOS INICIAIS (SEED DATA)
-- ============================================================================

-- ----------------------------------------------------------------------------
-- Dados Iniciais: Grupos de Usuários
-- ----------------------------------------------------------------------------
INSERT INTO grupos_usuarios (id_grupo, nome_grupo, descricao, nivel_acesso) VALUES
(gerar_id_grupo(), 'Administradores', 'Acesso total ao sistema, incluindo configurações e gerenciamento de usuários', 5),
(gerar_id_grupo(), 'Bibliotecários', 'Gerenciamento completo de obras, empréstimos e usuários', 4),
(gerar_id_grupo(), 'Atendentes', 'Operações de empréstimo, devolução e atendimento ao público', 3),
(gerar_id_grupo(), 'Leitores Premium', 'Usuários com privilégios estendidos de empréstimo', 2),
(gerar_id_grupo(), 'Leitores Básicos', 'Usuários padrão com acesso básico ao acervo', 1);

-- ----------------------------------------------------------------------------
-- Dados Iniciais: Categorias
-- ----------------------------------------------------------------------------
INSERT INTO categorias (id_categoria, nome_categoria, descricao, nivel_restricao) VALUES
(gerar_id_categoria(), 'Codex Astartes', 'Tratados militares e estratégias de combate dos Space Marines', 2),
(gerar_id_categoria(), 'Histórias do Imperium', 'Crônicas e registros históricos do Império da Humanidade', 0),
(gerar_id_categoria(), 'Textos Heréticos', 'Documentos sobre o Caos e forças inimigas - Acesso Restrito', 5),
(gerar_id_categoria(), 'Mechanicus', 'Conhecimentos técnicos e sagrados do Adeptus Mechanicus', 3),
(gerar_id_categoria(), 'Literatura Imperial', 'Obras literárias e culturais do Imperium', 0),
(gerar_id_categoria(), 'Inquisição', 'Registros e investigações da Santa Inquisição', 4),
(gerar_id_categoria(), 'Xenos', 'Estudos sobre raças alienígenas', 3);

-- ----------------------------------------------------------------------------
-- Dados Iniciais: Autores
-- ----------------------------------------------------------------------------
INSERT INTO autores (id_autor, nome_autor, biografia, nacionalidade) VALUES
(gerar_id_autor(), 'Roboute Guilliman', 'Primarca dos Ultramarines e autor do sagrado Codex Astartes', 'Macragge'),
(gerar_id_autor(), 'Belisarius Cawl', 'Arquimagos do Adeptus Mechanicus, inventor dos Primaris Marines', 'Marte'),
(gerar_id_autor(), 'Inquisidor Gregor Eisenhorn', 'Renomado Inquisidor e autor de diversos tratados sobre heresia', 'Terra'),
(gerar_id_autor(), 'Ciaphas Cain', 'Comissário da Guarda Imperial, herói relutante', 'Terra'),
(gerar_id_autor(), 'Malcador, o Sigillita', 'Primeiro Senhor da Terra e conselheiro do Imperador', 'Terra');

-- ----------------------------------------------------------------------------
-- Dados Iniciais: Usuários
-- Nota: As senhas são hashes fictícios. Em produção, usar bcrypt ou similar.
-- ----------------------------------------------------------------------------
INSERT INTO usuarios (id_usuario, nome_completo, email, senha_hash, id_grupo) VALUES
(gerar_id_usuario(), 'Samuel Telles de Vasconcellos Resende', 'samuel.resende@biblioimp.org', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhLu', 
 (SELECT id_grupo FROM grupos_usuarios WHERE nome_grupo = 'Administradores')),

(gerar_id_usuario(), 'Rafael Machado dos Santos', 'rafael.santos@biblioimp.org',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhLu',
 (SELECT id_grupo FROM grupos_usuarios WHERE nome_grupo = 'Bibliotecários')),

(gerar_id_usuario(), 'Raphael Ryan Pires Simão', 'raphael.simao@biblioimp.org',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhLu',
 (SELECT id_grupo FROM grupos_usuarios WHERE nome_grupo = 'Bibliotecários')),

(gerar_id_usuario(), 'Yurik Alexsander Soares Feitosa', 'yurik.feitosa@biblioimp.org',
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhLu',
 (SELECT id_grupo FROM grupos_usuarios WHERE nome_grupo = 'Atendentes'));

-- ----------------------------------------------------------------------------
-- Dados Iniciais: Obras
-- ----------------------------------------------------------------------------
INSERT INTO obras (id_obra, titulo, subtitulo, id_categoria, isbn, ano_publicacao, editora, idioma, num_paginas, sinopse, localizacao_fisica) VALUES
(gerar_id_obra(), 'Codex Astartes', 'O Tratado Definitivo de Guerra Espacial', 
 (SELECT id_categoria FROM categorias WHERE nome_categoria = 'Codex Astartes'),
 '978-1-84154-000-0', 30000, 'Editora Imperial', 'Alto Gótico', 1200,
 'O tratado militar definitivo escrito por Roboute Guilliman, estabelecendo as diretrizes de organização e combate dos Space Marines.',
 'Setor Alpha - Prateleira A1'),

(gerar_id_obra(), 'A Heresia de Horus', 'A Maior Traição da História Imperial',
 (SELECT id_categoria FROM categorias WHERE nome_categoria = 'Histórias do Imperium'),
 '978-1-84154-001-7', 31000, 'Editora Imperial', 'Alto Gótico', 2500,
 'Relato completo da traição de Horus e da guerra civil que quase destruiu o Imperium.',
 'Setor Beta - Prateleira B3'),

(gerar_id_obra(), 'Mistérios do Mechanicus', 'Princípios Sagrados da Tecnologia',
 (SELECT id_categoria FROM categorias WHERE nome_categoria = 'Mechanicus'),
 '978-1-84154-002-4', 35000, 'Editora Marciana', 'Binário Sagrado', 800,
 'Compêndio dos conhecimentos técnicos e rituais do Adeptus Mechanicus.',
 'Setor Gamma - Prateleira C2'),

(gerar_id_obra(), 'Xenos: Uma Ameaça Constante', 'Catálogo de Raças Alienígenas',
 (SELECT id_categoria FROM categorias WHERE nome_categoria = 'Xenos'),
 '978-1-84154-003-1', 40000, 'Editora Imperial', 'Baixo Gótico', 600,
 'Estudo detalhado sobre as principais raças xenos que ameaçam o Imperium.',
 'Setor Delta - Prateleira D1');

-- ----------------------------------------------------------------------------
-- Relacionamento: Obras e Autores
-- ----------------------------------------------------------------------------
INSERT INTO obras_autores (id_obra, id_autor, ordem_autoria) VALUES
((SELECT id_obra FROM obras WHERE titulo = 'Codex Astartes'), 
 (SELECT id_autor FROM autores WHERE nome_autor = 'Roboute Guilliman'), 1),

((SELECT id_obra FROM obras WHERE titulo = 'A Heresia de Horus'),
 (SELECT id_autor FROM autores WHERE nome_autor = 'Malcador, o Sigillita'), 1),

((SELECT id_obra FROM obras WHERE titulo = 'Mistérios do Mechanicus'),
 (SELECT id_autor FROM autores WHERE nome_autor = 'Belisarius Cawl'), 1),

((SELECT id_obra FROM obras WHERE titulo = 'Xenos: Uma Ameaça Constante'),
 (SELECT id_autor FROM autores WHERE nome_autor = 'Inquisidor Gregor Eisenhorn'), 1);

-- ----------------------------------------------------------------------------
-- Dados Iniciais: Exemplares
-- ----------------------------------------------------------------------------
INSERT INTO exemplares (id_exemplar, id_obra, codigo_barras, estado_conservacao, disponivel, data_aquisicao, valor_aquisicao) VALUES
(gerar_id_exemplar(), (SELECT id_obra FROM obras WHERE titulo = 'Codex Astartes'),
 'BIMP-0001-001', 'Excelente', TRUE, '2024-01-15', 250.00),

(gerar_id_exemplar(), (SELECT id_obra FROM obras WHERE titulo = 'Codex Astartes'),
 'BIMP-0001-002', 'Bom', TRUE, '2024-01-15', 250.00),

(gerar_id_exemplar(), (SELECT id_obra FROM obras WHERE titulo = 'A Heresia de Horus'),
 'BIMP-0002-001', 'Excelente', TRUE, '2024-02-10', 450.00),

(gerar_id_exemplar(), (SELECT id_obra FROM obras WHERE titulo = 'Mistérios do Mechanicus'),
 'BIMP-0003-001', 'Bom', TRUE, '2024-03-05', 180.00),

(gerar_id_exemplar(), (SELECT id_obra FROM obras WHERE titulo = 'Xenos: Uma Ameaça Constante'),
 'BIMP-0004-001', 'Regular', TRUE, '2024-03-20', 120.00);

-- SEÇÃO 10: COMENTÁRIOS FINAIS E BREVE DOCUMENTAÇÃO

-- Este script SQL implementa todos os requisitos obrigatórios do trabalho:
--
-- 1.  Estrutura completa de tabelas com relacionamentos
-- 2.  Índices com justificativas (12 índices criados)
-- 3.  Triggers (mínimo 2, implementados 6)
-- 4.  Views (mínimo 2, implementadas 4)
-- 5.  Procedures e Functions (mínimo 2, implementadas 4 procedures e 11 functions)
-- 6.  Usuários e controle de acesso (5 usuários com diferentes níveis)
-- 7.  Tabelas obrigatórias: usuarios e grupos_usuarios
-- 8.  Geração de IDs customizada (funções específicas para cada tabela)
-- 9.  Justificativa para uso de AUTO_INCREMENT (apenas em logs_sistema)
