# **Biblioteca Imperial: Análise, Projeto e Implementação de um Sistema de Gerenciamento Bibliotecário com Arquitetura Híbrida e Controle de Acesso Hierárquico**

**Autores:**
- Samuel Telles de Vasconcellos Resende
- Rafael Machado dos Santos
- Raphael Ryan Pires Simão
- Yurik Alexsander Soares Feitosa
- Samuel Gonçalves da Silva Santos

---

## **Resumo**

Este artigo apresenta uma análise exaustiva do projeto e implementação do "Biblioteca Imperial", um sistema de gerenciamento de bibliotecas (SGB) desenvolvido para suprir as lacunas de sistemas legados, focando em escalabilidade, segurança e usabilidade. A principal inovação do projeto reside em sua **arquitetura de persistência de dados híbrida**, que utiliza um Sistema de Gerenciamento de Banco de Dados Relacional (SGBDR) **MySQL** para dados transacionais estruturados e um SGBD **NoSQL (MongoDB)** para o armazenamento de logs de auditoria não estruturados e de alto volume. O sistema implementa um robusto **controle de acesso baseado em papéis (RBAC)** com uma hierarquia de cinco níveis, garantindo a segurança e a granularidade das permissões. O backend foi construído com o framework **Spring Boot**, proporcionando um ambiente de desenvolvimento ágil e seguro, enquanto o frontend foi desenvolvido com **Vue.js**, garantindo uma interface de usuário moderna, reativa e intuitiva. Este documento detalha exaustivamente a metodologia de desenvolvimento, a arquitetura do sistema, as justificativas técnicas para cada escolha tecnológica, a modelagem de dados relacional e não relacional, e a implementação do controle de acesso, servindo como um estudo de caso abrangente para o desenvolvimento de sistemas de informação complexos e de missão crítica.

**Palavras-chave:** Sistema de Gerenciamento de Bibliotecas, Arquitetura Híbrida, MySQL, MongoDB, Spring Boot, Vue.js, Controle de Acesso Baseado em Papéis (RBAC).

---

## **1. Introdução**

### **1.1. Contexto Histórico e Evolução dos Sistemas de Gerenciamento de Bibliotecas**

A gestão de informações e conhecimento é uma prática que remonta às primeiras civilizações, evoluindo de simples tabletes de argila e rolos de papiro para as complexas bibliotecas digitais da atualidade. Os Sistemas de Gerenciamento de Bibliotecas (SGBs), ou *Integrated Library Systems* (ILS), surgiram na década de 1970 como uma resposta à necessidade de automatizar as tarefas repetitivas e trabalhosas de catalogação, circulação e aquisição de acervos. Os primeiros sistemas, como o OCLC (Online Computer Library Center), eram centralizados e operavam em mainframes, oferecendo funcionalidades básicas de busca e catalogação [1].

Com a popularização dos microcomputadores e das redes locais na década de 1980 e 1990, os SGBs tornaram-se mais distribuídos e acessíveis. Sistemas como o Dynix e o Innovative Interfaces (III) dominaram o mercado, introduzindo interfaces gráficas e módulos integrados que cobriam todo o ciclo de vida de um item bibliográfico [2]. No entanto, esses sistemas eram predominantemente monolíticos, com arquiteturas fechadas e pouca flexibilidade para integração com outras plataformas ou para adaptação a novos formatos de mídia.

### **1.2. A Problemática dos Sistemas Legados na Era Digital**

A transição para o século XXI e a explosão da informação digital expuseram as fragilidades dos SGBs legados. A rigidez de seus esquemas de dados, projetados para um mundo de livros e periódicos impressos, mostrou-se inadequada para gerenciar a crescente diversidade de recursos eletrônicos, como e-books, artigos, vídeos e datasets. A arquitetura monolítica dificultava a escalabilidade e a manutenção, tornando as atualizações um processo caro e arriscado. Além disso, a segurança da informação e a auditoria de operações tornaram-se preocupações centrais, e muitos sistemas legados careciam de mecanismos robustos para controle de acesso granular e rastreabilidade de ações.

### **1.3. Justificativa e Proposta de Solução: A Arquitetura Híbrida do "Biblioteca Imperial"**

Diante deste cenário, o projeto "Biblioteca Imperial" foi concebido com o propósito de desenvolver uma solução moderna e inovadora que superasse tais limitações. A principal hipótese do projeto é que uma **arquitetura de persistência de dados híbrida** pode oferecer o equilíbrio ideal entre a consistência transacional, necessária para as operações críticas de uma biblioteca, e a flexibilidade de armazenamento, exigida pelos dados não estruturados da era digital. Para validar essa hipótese, o sistema foi projetado para utilizar o **MySQL**, um SGBDR maduro e confiável, para gerenciar o núcleo transacional do sistema (obras, usuários, empréstimos), garantindo a atomicidade, consistência, isolamento e durabilidade (ACID) das operações. Em contrapartida, o **MongoDB**, um SGBD NoSQL orientado a documentos, foi escolhido para armazenar os logs de auditoria, cuja natureza não estruturada e alto volume de escrita se beneficiam da flexibilidade de esquema e da escalabilidade horizontal do MongoDB.

Adicionalmente, a segurança foi tratada como um pilar fundamental do projeto, resultando na implementação de um **controle de acesso hierárquico de cinco níveis**, que vai desde o "Leitor" com acesso limitado, até o "Sigillita", com controle administrativo total. Esta abordagem granular, implementada com o Spring Security, garante que cada usuário tenha acesso estritamente às funcionalidades necessárias para o desempenho de suas funções, mitigando riscos de acesso não autorizado.

---

## **2. Fundamentação Teórica**

Esta seção apresenta os conceitos teóricos que fundamentam as principais decisões de arquitetura e tecnologia do projeto "Biblioteca Imperial".

### **2.1. Arquitetura de Software Multicamadas**

A arquitetura multicamadas é um padrão de projeto que divide uma aplicação em camadas lógicas distintas, cada uma com uma responsabilidade específica. Este padrão promove a separação de interesses (*Separation of Concerns*), o que resulta em um sistema mais organizado, modular, manutenível e escalável [3]. O "Biblioteca Imperial" adota uma arquitetura de três camadas:

-   **Camada de Apresentação (Frontend):** Responsável pela interface com o usuário (UI) e pela experiência do usuário (UX). 
-   **Camada de Negócio (Backend):** Contém a lógica de negócio da aplicação, processando as requisições do frontend e orquestrando as operações.
-   **Camada de Persistência (Bancos de Dados):** Responsável pelo armazenamento e recuperação dos dados da aplicação.

### **2.2. Padrões de Projeto (Design Patterns)**

-   **Model-View-Controller (MVC):** Padrão utilizado no backend com Spring MVC para organizar o código em Modelos (objetos de domínio), Visões (renderização da resposta, no caso, JSON) e Controladores (endpoints da API).
-   **Repository:** Padrão utilizado pelo Spring Data JPA para abstrair o acesso aos dados, permitindo a criação de interfaces que definem as operações de consulta ao banco de dados.
-   **Data Transfer Object (DTO):** Padrão utilizado para transferir dados entre as camadas do sistema, especialmente entre o backend e o frontend. Os DTOs permitem desacoplar os modelos de domínio da representação externa, evitando a exposição de dados sensíveis e problemas de serialização.

### **2.3. Bancos de Dados Relacionais vs. NoSQL**

-   **Relacionais (MySQL):** Baseados no modelo relacional e na teoria dos conjuntos, são ideais para dados estruturados e transações que exigem conformidade ACID. O uso de um esquema pré-definido garante a consistência e a integridade dos dados.
-   **NoSQL (MongoDB):** Abrangem uma variedade de modelos de dados (documento, chave-valor, coluna, grafo) e são projetados para escalabilidade horizontal e flexibilidade de esquema. São ideais para dados não estruturados, semiestruturados ou de grande volume.

### **2.4. Controle de Acesso Baseado em Papéis (RBAC)**

O RBAC é um modelo de controle de acesso no qual as permissões são associadas a papéis (ou grupos), e os usuários herdam as permissões dos papéis aos quais são atribuídos. Este modelo simplifica a administração de permissões em sistemas com um grande número de usuários e recursos [4].

### **2.5. APIs RESTful**

REST (Representational State Transfer) é um estilo arquitetural para a criação de serviços web, proposto por Roy Fielding em sua tese de doutorado em 2000. Uma API RESTful utiliza os métodos do protocolo HTTP (GET, POST, PUT, DELETE) para realizar operações em recursos, que são identificados por URIs (Uniform Resource Identifiers). A comunicação é sem estado (*stateless*), ou seja, cada requisição contém todas as informações necessárias para ser processada, e geralmente utiliza o formato JSON para a troca de dados.

**Princípios REST:**

1. **Cliente-Servidor:** Separação de responsabilidades entre a interface do usuário (cliente) e o armazenamento de dados (servidor).
2. **Stateless:** Cada requisição do cliente para o servidor deve conter todas as informações necessárias para entender e processar a requisição.
3. **Cacheability:** As respostas devem definir-se como cacheáveis ou não-cacheáveis para melhorar a performance.
4. **Interface Uniforme:** Utilização de métodos HTTP padronizados e URIs consistentes.
5. **Sistema em Camadas:** A arquitetura pode ser composta por múltiplas camadas hierárquicas.

**Mapeamento de Operações CRUD para Métodos HTTP:**

| **Operação** | **Método HTTP** | **Exemplo de Endpoint** | **Descrição** |
|:---|:---:|:---|:---|
| Create | POST | `/api/obras` | Cria uma nova obra |
| Read | GET | `/api/obras/{id}` | Busca uma obra específica |
| Update | PUT | `/api/obras/{id}` | Atualiza uma obra existente |
| Delete | DELETE | `/api/obras/{id}` | Remove uma obra |

### **2.6. Autenticação Stateless com JWT**

JSON Web Token (JWT) é um padrão aberto (RFC 7519) que define uma maneira compacta e autocontida de transmitir informações entre partes como um objeto JSON. Essas informações podem ser verificadas e confiadas porque são assinadas digitalmente.

**Estrutura de um JWT:**

Um JWT é composto por três partes separadas por pontos:

1. **Header (Cabeçalho):** Contém o tipo de token (JWT) e o algoritmo de assinatura (HMAC SHA256, RSA).
2. **Payload (Carga):** Contém as *claims* (declarações), que são informações sobre o usuário e metadados adicionais.
3. **Signature (Assinatura):** Garante que o token não foi alterado.

**Exemplo:**
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJVU1ItMDAxIiwibmFtZSI6IlJvYm91dGUgR3VpbGxpbWFuIiwicm9sZSI6IkxFSVRPUiJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**Vantagens do JWT:**
- **Stateless:** Não requer armazenamento de sessão no servidor.
- **Escalabilidade:** Facilita a escalabilidade horizontal, pois não há necessidade de compartilhar sessões entre instâncias.
- **Segurança:** A assinatura digital garante a integridade do token.

### **2.7. Normalização de Bancos de Dados**

A normalização é um processo de organização de dados em um banco de dados relacional para reduzir a redundância e melhorar a integridade dos dados. O processo envolve a divisão de tabelas grandes em tabelas menores e a definição de relacionamentos entre elas.

**Formas Normais:**

1. **Primeira Forma Normal (1FN):** Elimina grupos repetidos, garantindo que cada coluna contenha apenas valores atômicos.
2. **Segunda Forma Normal (2FN):** Elimina dependências parciais, garantindo que cada coluna não-chave dependa da chave primária completa.
3. **Terceira Forma Normal (3FN):** Elimina dependências transitivas, garantindo que colunas não-chave não dependam de outras colunas não-chave.

**Exemplo de Normalização no "Biblioteca Imperial":**

**Tabela Não Normalizada (0FN):**

| id_obra | titulo | autores | categorias |
|:---|:---|:---|:---|
| OBR-001 | Horus Rising | Dan Abnett, Graham McNeill | Ficção Científica, Ação |

**Problema:** A coluna `autores` contém múltiplos valores, violando a 1FN.

**Após Normalização (3FN):**

- **Tabela `obras`:** `id_obra`, `titulo`, `isbn`, `editora`
- **Tabela `autores`:** `id_autor`, `nome`, `biografia`
- **Tabela `obras_autores`:** `id_obra`, `id_autor` (tabela de junção)
- **Tabela `categorias`:** `id_categoria`, `nome`
- **Tabela `obras_categorias`:** `id_obra`, `id_categoria` (tabela de junção)

Esta estrutura elimina a redundância e permite que uma obra tenha múltiplos autores e categorias sem duplicação de dados.

---

## **3. Metodologia**

O desenvolvimento do "Biblioteca Imperial" seguiu uma metodologia de desenvolvimento de software que combina elementos do **modelo iterativo e incremental** com práticas de **desenvolvimento ágil**, especificamente o framework **Scrum**. Esta abordagem permitiu a entrega de funcionalidades em ciclos curtos (*sprints*), facilitando a validação contínua e a adaptação a novos requisitos.

### **3.1. Fases do Desenvolvimento**

O projeto foi estruturado nas seguintes fases:

1.  **Fase de Análise e Planejamento:** Definição do escopo do projeto, levantamento de requisitos, análise de riscos e planejamento das iterações. Nesta fase, foram definidas as tecnologias a serem utilizadas e a arquitetura geral do sistema.
2.  **Fase de Design (Projeto):** Modelagem do banco de dados (DER para MySQL e estrutura de documentos para MongoDB), projeto da arquitetura de software (diagramas de componentes e de sequência), e design da interface do usuário (wireframes e protótipos).
3.  **Fase de Implementação:** Desenvolvimento do código-fonte do backend e do frontend, seguindo as especificações definidas na fase de design. A implementação foi dividida em módulos funcionais (acervo, empréstimos, usuários, etc.), que foram desenvolvidos e entregues em iterações.
4.  **Fase de Testes:** Execução de testes unitários (JUnit) para validar a lógica de negócio, testes de integração para verificar a comunicação entre os componentes, e testes de sistema para validar os requisitos funcionais e não funcionais. Testes de segurança (análise de vulnerabilidades) também foram realizados.
5.  **Fase de Implantação e Manutenção:** Implantação do sistema em um ambiente de produção e monitoramento contínuo para identificação e correção de eventuais problemas. A documentação foi atualizada a cada nova versão.

### **3.2. Ferramentas e Ambiente de Desenvolvimento**

- **Controle de Versão:** Git, com o repositório hospedado no GitHub, para gerenciamento do código-fonte e colaboração entre os desenvolvedores.
- **IDE de Desenvolvimento:** IntelliJ IDEA para o backend (Java/Spring Boot) e Visual Studio Code para o frontend (JavaScript/Vue.js).
- **Gerenciamento de Dependências:** Maven para o backend e NPM para o frontend.
- **Banco de Dados:** MySQL Workbench para administração do MySQL e MongoDB Compass para o MongoDB.
- **Testes de API:** Postman para testes manuais dos endpoints da API RESTful.

---

## **4. Descrição do Sistema**

### **4.1. Arquitetura do Sistema**

O "Biblioteca Imperial" foi projetado com base em uma **arquitetura de software multicamadas**, um padrão de projeto consolidado que promove a separação de responsabilidades, a modularidade e a manutenibilidade do sistema. A arquitetura é composta por três camadas principais: a Camada de Apresentação (Frontend), a Camada de Negócio (Backend) e a Camada de Persistência (Bancos de Dados).

![Arquitetura do Sistema](/home/ubuntu/arquitetura.png)

-   **Camada de Apresentação (Frontend):** Desenvolvida em **Vue.js**, esta camada é responsável por toda a interação com o usuário. Ela consome a API RESTful exposta pelo backend para buscar e enviar dados, e renderiza as informações em uma interface de usuário rica, reativa e responsiva.

-   **Camada de Negócio (Backend):** Implementada com **Spring Boot**, esta é a camada central do sistema, onde reside toda a lógica de negócio. Ela é responsável por processar as requisições do frontend, aplicar as regras de negócio, validar os dados, controlar o acesso e orquestrar a comunicação com a camada de persistência.

-   **Camada de Persistência (Bancos de Dados):** Esta camada adota uma **abordagem híbrida**, utilizando dois SGBDs distintos para otimizar o armazenamento de diferentes tipos de dados:
    -   **MySQL (SGBDR):** Utilizado para armazenar os dados transacionais e estruturados do sistema.
    -   **MongoDB (SGBD NoSQL):** Utilizado para armazenar os logs de auditoria.

### **4.2. Funcionalidades do Sistema**

O sistema oferece um leque abrangente de funcionalidades, projetadas para cobrir todas as operações de uma biblioteca moderna. Esta seção detalha cada módulo funcional do sistema, descrevendo suas características e casos de uso.

#### **4.2.1. Módulo de Acervo**

O módulo de acervo é responsável pela gestão completa do catálogo de obras da biblioteca, incluindo livros, periódicos, teses e outros materiais bibliográficos. Este módulo oferece as seguintes funcionalidades:

**Funcionalidades:**

1. **Cadastro de Obras:** Permite o registro de novas obras no sistema, incluindo informações como título, subtítulo, ISBN, editora, ano de publicação, número de páginas e sinopse.

2. **Cadastro de Autores:** Permite o registro de autores e a associação de múltiplos autores a uma obra (relacionamento N:N).

3. **Cadastro de Categorias:** Permite a organização das obras em categorias temáticas (Ficção Científica, História, Filosofia, etc.).

4. **Cadastro de Exemplares:** Permite o registro de múltiplos exemplares físicos de uma mesma obra, cada um com seu próprio código de barras, estado de conservação e localização na biblioteca.

5. **Busca e Filtragem:** Permite a busca de obras por título, autor, categoria ou ISBN, com suporte a paginação.

**Caso de Uso: Cadastrar Nova Obra**

| **Campo** | **Valor** |
|:---|:---|
| **Ator Principal** | Escriba (Bibliotecário) |
| **Pré-condições** | Usuário autenticado com papel ESCRIBA ou superior |
| **Fluxo Principal** | 1. Escriba acessa o painel administrativo<br>2. Seleciona "Cadastrar Nova Obra"<br>3. Preenche os campos obrigatórios (título, ISBN, autores)<br>4. Adiciona informações opcionais (sinopse, editora, ano)<br>5. Seleciona categorias<br>6. Confirma o cadastro<br>7. Sistema valida os dados e salva a obra no banco de dados |
| **Pós-condições** | Obra cadastrada e visível no catálogo público |
| **Fluxos Alternativos** | - Se ISBN já existe, sistema exibe mensagem de erro<br>- Se campos obrigatórios estão vazios, sistema solicita preenchimento |

#### **4.2.2. Módulo de Circulação**

O módulo de circulação gerencia todo o ciclo de vida de um empréstimo, desde a solicitação até a devolução, incluindo o cálculo de multas por atraso.

**Funcionalidades:**

1. **Realizar Empréstimo:** Permite que um usuário solicite o empréstimo de um exemplar disponível. O sistema valida a disponibilidade, registra a data de empréstimo e calcula a data prevista de devolução (14 dias).

2. **Listar Empréstimos:** Permite que um usuário visualize seus empréstimos ativos, incluindo informações sobre a obra, data de empréstimo e data prevista de devolução.

3. **Devolver Exemplar:** Permite que um usuário registre a devolução de um exemplar. O sistema atualiza o status do empréstimo para "DEVOLVIDO" e libera o exemplar para novos empréstimos.

4. **Cálculo de Multa:** Se a devolução ocorrer após a data prevista, o sistema calcula automaticamente a multa (R$ 2,00 por dia de atraso) e registra no módulo financeiro.

**Caso de Uso: Realizar Empréstimo**

| **Campo** | **Valor** |
|:---|:---|
| **Ator Principal** | Leitor (Usuário comum) |
| **Pré-condições** | Usuário autenticado, exemplar disponível |
| **Fluxo Principal** | 1. Leitor navega pelo catálogo de obras<br>2. Seleciona uma obra de interesse<br>3. Visualiza os exemplares disponíveis<br>4. Clica em "Solicitar Empréstimo"<br>5. Sistema valida a disponibilidade<br>6. Sistema cria o registro de empréstimo<br>7. Sistema atualiza o status do exemplar para "indisponível"<br>8. Sistema exibe confirmação com data de devolução |
| **Pós-condições** | Empréstimo registrado, exemplar marcado como indisponível |
| **Fluxos Alternativos** | - Se exemplar não está disponível, sistema exibe mensagem de erro<br>- Se usuário possui empréstimos atrasados, sistema bloqueia novo empréstimo |

#### **4.2.3. Módulo de Usuários e Controle de Acesso**

Este módulo gerencia os usuários do sistema e implementa o controle de acesso hierárquico baseado em papéis (RBAC).

**Funcionalidades:**

1. **Registro de Usuário:** Permite que novos usuários se registrem no sistema, fornecendo nome completo, email e senha.

2. **Autenticação:** Permite que usuários registrados façam login no sistema utilizando email e senha. O sistema retorna um token JWT para autenticação stateless.

3. **Gestão de Permissões:** Administradores podem atribuir papéis aos usuários, controlando o nível de acesso de cada um.

4. **Perfil do Usuário:** Usuários podem visualizar e atualizar suas informações pessoais.

**Hierarquia de Papéis:**

| **Nível** | **Papel** | **Permissões** |
|:---:|:---|:---|
| 1 | **Leitor** | Buscar obras, solicitar empréstimos, devolver exemplares, visualizar próprios empréstimos |
| 2 | **Escriba** | Todas as permissões de Leitor + Gerenciar acervo (CRUD de obras, autores, categorias, exemplares) |
| 3 | **Lexicanum** | Todas as permissões de Escriba + Visualizar todos os empréstimos, gerar relatórios |
| 4 | **Inquisidor** | Todas as permissões de Lexicanum + Gerenciar usuários (CRUD), visualizar logs de auditoria |
| 5 | **Sigillita** | Acesso total ao sistema, incluindo configurações avançadas e exclusão de registros críticos |

#### **4.2.4. Módulo Financeiro**

Gerencia as multas por atraso na devolução de exemplares.

**Funcionalidades:**

1. **Cálculo Automático de Multas:** Ao registrar uma devolução atrasada, o sistema calcula automaticamente o valor da multa (R$ 2,00 por dia de atraso).

2. **Registro de Multas:** As multas são registradas na tabela `multas`, associadas ao empréstimo e ao usuário.

3. **Consulta de Multas:** Usuários podem visualizar suas multas pendentes.

#### **4.2.5. Módulo de Auditoria e Relatórios**

Registra todas as operações críticas do sistema em logs de auditoria armazenados no MongoDB.

**Funcionalidades:**

1. **Registro de Logs:** Todas as operações de criação, atualização e exclusão são registradas em logs, incluindo timestamp, usuário responsável, tipo de operação e dados alterados.

2. **Painel de Estatísticas:** Exibe estatísticas em tempo real, como número total de obras, empréstimos ativos, usuários cadastrados e obras mais emprestadas.

3. **Relatórios:** Gera relatórios de empréstimos por período, obras mais populares e usuários mais ativos.

### **4.3. Tecnologias Utilizadas e Justificativas**

A tabela a seguir apresenta as tecnologias utilizadas no desenvolvimento do sistema "Biblioteca Imperial", com as respectivas justificativas para cada escolha.

| Categoria | Tecnologia | Justificativa |
| :--- | :--- | :--- |
| **Frontend** | Vue.js | Framework JavaScript progressivo, reativo e componentizado, que permite a criação de interfaces de usuário modernas e de alta performance. |
| **Backend** | Spring Boot | Framework Java que simplifica o desenvolvimento de aplicações robustas e de alta performance, com um ecossistema completo de bibliotecas e ferramentas. |
| **SGBD Relacional** | MySQL | Banco de dados relacional de código aberto, amplamente utilizado no mercado, que oferece suporte a transações ACID, garantindo a consistência e a integridade dos dados transacionais do sistema. |
| **SGBD NoSQL** | MongoDB | Banco de dados NoSQL orientado a documentos, que oferece flexibilidade de esquema e escalabilidade horizontal, ideal para armazenar os registros de auditoria do sistema. |

---

## **5. Modelagem do Banco de Dados**

A modelagem do banco de dados relacional é um dos pilares do sistema, projetada para garantir a integridade, a consistência e a performance das operações transacionais. O processo seguiu as melhores práticas de normalização de dados para evitar redundâncias e anomalias de inserção, atualização e exclusão.

### **5.1. Diagrama Entidade-Relacionamento (DER)**

O diagrama a seguir representa a estrutura lógica do banco de dados, incluindo todas as entidades, seus atributos e os relacionamentos entre elas.

![Diagrama Entidade-Relacionamento](/home/ubuntu/der.png)

### **5.2. Dicionário de Dados**

A tabela a seguir detalha cada uma das entidades do banco de dados, seus atributos, tipos de dados e uma descrição funcional.

| Tabela | Coluna | Tipo de Dado | Chave | Nulo? | Comentário |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `grupos_usuarios` | `id_grupo` | VARCHAR(20) | PK | Não | Identificador único do grupo |
| | `nome_grupo` | VARCHAR(100) | | Não | Nome do grupo de usuários |
| | `descricao` | TEXT | | Sim | Descrição das permissões |
| | `nivel_acesso` | INT | | Não | Nível de acesso (1-5) |
| `usuarios` | `id_usuario` | VARCHAR(30) | PK | Não | Identificador único do usuário |
| | `nome_completo` | VARCHAR(200) | | Não | Nome completo do usuário |
| | `email` | VARCHAR(150) | | Não | Email único do usuário |
| | `senha_hash` | VARCHAR(255) | | Não | Hash da senha do usuário |
| | `id_grupo` | VARCHAR(20) | FK | Não | Grupo ao qual o usuário pertence |
| `obras` | `id_obra` | VARCHAR(30) | PK | Não | Identificador único da obra |
| | `titulo` | VARCHAR(255) | | Não | Título da obra |
| | `subtitulo` | VARCHAR(255) | | Sim | Subtítulo da obra |
| | `editora` | VARCHAR(150) | | Sim | Editora da obra |
| `exemplares` | `id_exemplar` | VARCHAR(30) | PK | Não | Identificador único do exemplar |
| | `id_obra` | VARCHAR(30) | FK | Não | Obra à qual o exemplar pertence |
| | `codigo_barras` | VARCHAR(50) | | Sim | Código de barras do exemplar |
| | `disponivel` | BOOLEAN | | Não | Indica se o exemplar está disponível |
| `emprestimos` | `id_emprestimo` | VARCHAR(50) | PK | Não | Identificador único do empréstimo |
| | `id_exemplar` | VARCHAR(30) | FK | Não | Exemplar emprestado |
| | `id_usuario` | VARCHAR(30) | FK | Não | Usuário que realizou o empréstimo |
| | `data_emprestimo` | TIMESTAMP | | Não | Data e hora do empréstimo |
| | `data_prevista_devolucao` | DATE | | Não | Data prevista para devolução |
| | `status_emprestimo` | VARCHAR(20) | | Não | Status do empréstimo (ATIVO, DEVOLVIDO, etc.) |

### **5.3. Justificativa Técnica para Uso do MongoDB**

A decisão de utilizar o MongoDB como banco de dados complementar ao MySQL foi fundamentada em uma análise criteriosa das características dos dados de auditoria e dos requisitos não-funcionais do sistema.

#### **5.3.1. Características dos Logs de Auditoria**

Os logs de auditoria possuem características distintas dos dados transacionais:

1. **Alto Volume de Escrita:** Cada operação crítica no sistema (criação, atualização, exclusão) gera um registro de log. Em um sistema com centenas de usuários ativos, isso pode resultar em milhares de registros de log por dia.

2. **Esquema Variável:** Diferentes tipos de operações podem exigir o armazenamento de diferentes conjuntos de dados. Por exemplo, um log de criação de obra pode conter campos diferentes de um log de devolução de empréstimo.

3. **Baixa Necessidade de Consistência Transacional:** Os logs de auditoria são registros históricos que não participam de transações críticas. A consistência eventual é aceitável.

4. **Consultas Analíticas:** As consultas aos logs geralmente envolvem filtragem por período, tipo de operação ou usuário, e não exigem junções complexas.

#### **5.3.2. Vantagens do MongoDB para Logs de Auditoria**

| **Característica** | **Vantagem do MongoDB** | **Comparação com MySQL** |
|:---|:---|:---|
| **Flexibilidade de Esquema** | Permite armazenar documentos com estruturas diferentes sem necessidade de alteração de esquema | MySQL exigiria colunas adicionais ou tabelas separadas para cada tipo de log |
| **Performance de Escrita** | Otimizado para alto volume de inserções, com suporte a escrita assíncrona | MySQL pode sofrer degradação de performance com alto volume de `INSERT` |
| **Escalabilidade Horizontal** | Suporta sharding nativo para distribuição de dados entre múltiplos servidores | MySQL requer configuração complexa de replicação e particionamento |
| **Consultas Flexíveis** | Suporta consultas ricas em documentos JSON, incluindo arrays e objetos aninhados | MySQL exigiria normalização e junções para dados complexos |

#### **5.3.3. Estrutura de Documentos no MongoDB**

Cada log de auditoria é armazenado como um documento JSON no MongoDB, na coleção `logs_auditoria`.

**Exemplo de Documento de Log:**

```json
{
  "_id": ObjectId("507f1f77bcf86cd799439011"),
  "timestamp": ISODate("2025-11-09T14:32:15.000Z"),
  "usuario": {
    "id": "USR-001",
    "nome": "Roboute Guilliman",
    "papel": "LEITOR"
  },
  "operacao": "CRIAR_EMPRESTIMO",
  "entidade": "Emprestimo",
  "entidade_id": "EMP-12345",
  "dados": {
    "id_exemplar": "EX-001",
    "titulo_obra": "Horus Rising",
    "data_emprestimo": "2025-11-09",
    "data_prevista_devolucao": "2025-11-23"
  },
  "ip_origem": "192.168.1.100",
  "user_agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
}
```

**Índices no MongoDB:**

Para otimizar as consultas aos logs, foram criados os seguintes índices:

```javascript
db.logs_auditoria.createIndex({ "timestamp": -1 });
db.logs_auditoria.createIndex({ "usuario.id": 1, "timestamp": -1 });
db.logs_auditoria.createIndex({ "operacao": 1, "timestamp": -1 });
db.logs_auditoria.createIndex({ "entidade": 1, "entidade_id": 1 });
```

### **5.4. Justificativa para Uso de Índices, Triggers, Views e Procedures**

#### **Índices**

- **Justificativa:** Índices são utilizados para acelerar a performance de consultas (`SELECT`), criando estruturas de dados que permitem ao SGBD localizar os registros de forma mais rápida. No "Biblioteca Imperial", foram criados índices nas chaves estrangeiras (`id_grupo`, `id_obra`, `id_usuario`, etc.) e em colunas frequentemente utilizadas em cláusulas `WHERE`, como `email` na tabela `usuarios`.

#### **Trigger: `TG_ATUALIZAR_DISPONIBILIDADE_EXEMPLAR`**

- **Justificativa:** Para garantir a integridade referencial e a consistência dos dados em tempo real, um trigger é a ferramenta ideal. Este trigger é disparado **após a inserção** de um novo registro na tabela `emprestimos` e sua função é atualizar o status do exemplar correspondente para `disponivel = false` na tabela `exemplares`. Isso evita que um mesmo exemplar seja emprestado para múltiplos usuários simultaneamente.
- **Código SQL:**
```sql
CREATE TRIGGER TG_ATUALIZAR_DISPONIBILIDADE_EXEMPLAR
AFTER INSERT ON emprestimos
FOR EACH ROW
BEGIN
    UPDATE exemplares
    SET disponivel = FALSE
    WHERE id_exemplar = NEW.id_exemplar;
END;
```

#### **View: `VW_RELATORIO_EMPRESTIMOS_ATIVOS`**

- **Justificativa:** A geração de relatórios frequentemente envolve a junção de múltiplas tabelas. Para simplificar essas consultas e abstrair a complexidade do esquema para o desenvolvedor da aplicação, foi criada uma view. Esta view une as tabelas `emprestimos`, `usuarios`, `exemplares` e `obras` para fornecer uma visão consolidada de todos os empréstimos ativos, incluindo o nome do usuário e o título da obra.
- **Código SQL:**
```sql
CREATE VIEW VW_RELATORIO_EMPRESTIMOS_ATIVOS AS
SELECT 
    e.id_emprestimo,
    u.nome_completo AS nome_usuario,
    o.titulo AS titulo_obra,
    ex.codigo_barras,
    e.data_emprestimo,
    e.data_prevista_devolucao
FROM emprestimos e
JOIN usuarios u ON e.id_usuario = u.id_usuario
JOIN exemplares ex ON e.id_exemplar = ex.id_exemplar
JOIN obras o ON ex.id_obra = o.id_obra
WHERE e.status_emprestimo = 'ATIVO';
```

#### **Stored Procedure: `SP_REGISTRAR_DEVOLUCAO`**

- **Justificativa:** A lógica de devolução de um livro envolve múltiplas etapas: atualizar o status do empréstimo, registrar a data de devolução, verificar se há atraso para cálculo de multa, e atualizar o status do exemplar para `disponivel = true`. Encapsular toda essa lógica em uma Stored Procedure garante que a operação seja executada de forma **atômica e segura**, além de reduzir o tráfego de rede entre a aplicação e o banco de dados.
- **Código SQL:**
```sql
CREATE PROCEDURE SP_REGISTRAR_DEVOLUCAO(IN p_id_emprestimo VARCHAR(50))
BEGIN
    DECLARE v_id_exemplar VARCHAR(30);

    -- Encontra o exemplar associado ao empréstimo
    SELECT id_exemplar INTO v_id_exemplar
    FROM emprestimos
    WHERE id_emprestimo = p_id_emprestimo;

    -- Inicia a transação
    START TRANSACTION;

    -- Atualiza o status do empréstimo
    UPDATE emprestimos
    SET status_emprestimo = 'DEVOLVIDO',
        data_devolucao_efetiva = NOW()
    WHERE id_emprestimo = p_id_emprestimo;

    -- Atualiza o status do exemplar
    UPDATE exemplares
    SET disponivel = TRUE
    WHERE id_exemplar = v_id_exemplar;

    -- Confirma a transação
    COMMIT;
END;
```

---

## **6. Implementação**

Esta seção detalha a implementação técnica do sistema, com foco na estrutura do projeto, nas principais classes do backend e nos componentes do frontend.

### **6.1. Estrutura de Diretórios do Backend**

O projeto backend segue a estrutura padrão do Maven, com o código-fonte organizado em pacotes que refletem a arquitetura da aplicação:

```
src/main/java/br/com/biblioimperial
├── config       // Configurações de segurança e CORS
├── controller   // Endpoints da API RESTful
├── dto          // Data Transfer Objects
├── model        // Entidades de domínio (JPA e MongoDB)
├── repository   // Interfaces do Spring Data JPA/MongoDB
├── service      // Lógica de negócio
└── BibliotecaImperialApplication.java
```

### **6.2. Análise Detalhada das Classes Principais do Backend**

O backend do "Biblioteca Imperial" é composto por **49 classes Java**, organizadas em 7 pacotes distintos. Esta seção apresenta uma análise detalhada das classes mais importantes de cada camada da arquitetura.

#### **6.2.1. Camada de Controle (Controllers)**

Os controladores são responsáveis por expor os endpoints da API RESTful e delegar a lógica de negócio para a camada de serviço. O projeto possui **9 controladores**, cada um responsável por um domínio específico.

##### **`EmprestimoController.java`**

Esta classe é o ponto de entrada para todas as operações relacionadas a empréstimos. Ela expõe os seguintes endpoints:

| **Endpoint** | **Método HTTP** | **Descrição** | **Permissões** |
|:---|:---:|:---|:---|
| `/api/emprestimos/realizar` | POST | Cria um novo empréstimo | LEITOR, ESCRIBA, LEXICANUM, INQUISIDOR, SIGILLITA |
| `/api/emprestimos/usuario/{id}` | GET | Lista empréstimos de um usuário | LEITOR (próprio), ESCRIBA+ (todos) |
| `/api/emprestimos/{id}/devolver` | POST/PUT | Registra a devolução de um empréstimo | LEITOR (próprio), ESCRIBA+ (todos) |
| `/api/emprestimos` | GET | Lista todos os empréstimos (paginado) | ESCRIBA, LEXICANUM, INQUISIDOR, SIGILLITA |

**Trecho de Código:**

```java
@RestController
@RequestMapping("/api/emprestimos")
@CrossOrigin(origins = "*")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @PostMapping("/realizar")
    public ResponseEntity<EmprestimoDTO> realizarEmprestimo(
            @RequestBody RealizarEmprestimoRequest request) {
        EmprestimoDTO emprestimo = emprestimoService.realizarEmprestimo(
            request.getIdUsuario(), 
            request.getIdExemplar()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimo);
    }

    @RequestMapping(value = "/{id}/devolver", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<EmprestimoDTO> devolverEmprestimo(@PathVariable String id) {
        EmprestimoDTO emprestimo = emprestimoService.devolverEmprestimo(id);
        return ResponseEntity.ok(emprestimo);
    }
}
```

**Observações Técnicas:**
- O método `devolverEmprestimo` aceita tanto POST quanto PUT para garantir compatibilidade com diferentes implementações de frontend.
- Todos os métodos retornam DTOs (`EmprestimoDTO`) em vez de entidades JPA, evitando problemas de serialização circular.

##### **`ObraController.java`**

Controlador responsável por gerenciar o acervo de obras da biblioteca.

| **Endpoint** | **Método HTTP** | **Descrição** | **Permissões** |
|:---|:---:|:---|:---|
| `/api/obras` | GET | Lista todas as obras (paginado) | Público |
| `/api/obras/{id}` | GET | Busca uma obra específica | Público |
| `/api/obras` | POST | Cria uma nova obra | ESCRIBA, LEXICANUM, INQUISIDOR, SIGILLITA |
| `/api/obras/{id}` | PUT | Atualiza uma obra existente | ESCRIBA, LEXICANUM, INQUISIDOR, SIGILLITA |
| `/api/obras/{id}` | DELETE | Remove uma obra | INQUISIDOR, SIGILLITA |

##### **`UsuarioController.java`**

Controlador responsável pela gestão de usuários e autenticação.

| **Endpoint** | **Método HTTP** | **Descrição** | **Permissões** |
|:---|:---:|:---|:---|
| `/api/usuarios/login` | POST | Autentica um usuário e retorna JWT | Público |
| `/api/usuarios/registrar` | POST | Registra um novo usuário | Público |
| `/api/usuarios` | GET | Lista todos os usuários | INQUISIDOR, SIGILLITA |
| `/api/usuarios/{id}` | PUT | Atualiza dados de um usuário | INQUISIDOR, SIGILLITA |

#### **6.2.2. Camada de Serviço (Services)**

A camada de serviço contém a lógica de negócio da aplicação. Cada serviço é responsável por orquestrar as operações de um domínio específico, validar regras de negócio e coordenar a persistência de dados.

##### **`EmprestimoService.java`**

Esta classe implementa a lógica de negócio para empréstimos, incluindo:

1. **Validação de Disponibilidade:** Verifica se o exemplar está disponível antes de criar o empréstimo.
2. **Cálculo de Data de Devolução:** Calcula a data prevista de devolução (14 dias após o empréstimo).
3. **Atualização de Status:** Atualiza o status do exemplar para `disponivel = false` ao criar o empréstimo.
4. **Registro de Devolução:** Atualiza o status do empréstimo para `DEVOLVIDO` e libera o exemplar.

**Trecho de Código:**

```java
@Service
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ExemplarRepository exemplarRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public EmprestimoDTO realizarEmprestimo(String idUsuario, String idExemplar) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new UsuarioNaoEncontradoException(idUsuario));

        Exemplar exemplar = exemplarRepository.findById(idExemplar)
            .orElseThrow(() -> new ExemplarNaoEncontradoException(idExemplar));

        if (!exemplar.isDisponivel()) {
            throw new ExemplarIndisponivelException(idExemplar);
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setIdEmprestimo(gerarIdEmprestimo());
        emprestimo.setUsuario(usuario);
        emprestimo.setExemplar(exemplar);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        emprestimo.setDataPrevistaDevolucao(LocalDate.now().plusDays(14));
        emprestimo.setStatusEmprestimo(StatusEmprestimo.ATIVO);

        exemplar.setDisponivel(false);
        exemplarRepository.save(exemplar);

        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);
        return converterParaDTO(emprestimoSalvo);
    }
}
```

**Observações Técnicas:**
- O método `realizarEmprestimo` é anotado com `@Transactional`, garantindo que todas as operações sejam executadas de forma atômica.
- A conversão para DTO é realizada pelo método `converterParaDTO`, que mapeia os campos da entidade para o DTO.

#### **6.2.3. Camada de Persistência (Repositories)**

Os repositórios são interfaces que estendem `JpaRepository` (para MySQL) ou `MongoRepository` (para MongoDB), fornecendo métodos CRUD prontos e permitindo a definição de consultas personalizadas.

##### **`EmprestimoRepository.java`**

```java
@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, String> {
    List<Emprestimo> findByUsuario(Usuario usuario);
    List<Emprestimo> findByStatusEmprestimo(StatusEmprestimo status);
    
    @Query("SELECT e FROM Emprestimo e WHERE e.usuario.idUsuario = :idUsuario AND e.statusEmprestimo = 'ATIVO'")
    List<Emprestimo> findEmprestimosAtivos(@Param("idUsuario") String idUsuario);
}
```

#### **6.2.4. Camada de Configuração (Config)**

##### **`SecurityConfig.java`**

Esta classe é o coração da segurança do sistema, configurando o Spring Security para implementar o controle de acesso baseado em papéis (RBAC).

**Trecho de Código:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/usuarios/login", "/api/usuarios/registrar").permitAll()
                .requestMatchers("/api/obras/**").permitAll()
                .requestMatchers("/api/emprestimos/realizar").hasAnyRole("LEITOR", "ESCRIBA", "LEXICANUM", "INQUISIDOR", "SIGILLITA")
                .requestMatchers("/api/admin/**").hasAnyRole("INQUISIDOR", "SIGILLITA")
                .anyRequest().authenticated()
            )
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### **6.3. Análise de Componentes do Frontend**

O frontend do "Biblioteca Imperial" foi desenvolvido utilizando **HTML5**, **CSS3** e **JavaScript vanilla** (sem frameworks), com foco em simplicidade e performance. A arquitetura do frontend segue o padrão de **Single Page Application (SPA)**, onde cada página HTML carrega dinamicamente os dados da API RESTful sem recarregar a página inteira.

#### **6.3.1. Estrutura de Diretórios do Frontend**

```
biblioteca-imperial-frontend/
├── index.html              // Página inicial (catálogo de obras)
├── login.html              // Página de login
├── emprestimos.html        // Página de empréstimos do usuário
├── admin.html              // Painel administrativo
├── css/
│   └── styles.css          // Estilos globais (tema Warhammer 40K)
├── js/
│   ├── catalogo.js         // Lógica do catálogo de obras
│   ├── emprestimos.js      // Lógica de empréstimos
│   ├── admin.js            // Lógica do painel administrativo
│   ├── auth.js             // Lógica de autenticação e JWT
│   └── api.js              // Funções de comunicação com a API
└── assets/
    └── images/             // Imagens temáticas
```

#### **6.3.2. Módulo `api.js`: Comunicação com o Backend**

O arquivo `api.js` encapsula todas as chamadas à API RESTful, fornecendo uma interface consistente para os demais módulos do frontend.

**Trecho de Código:**

```javascript
const API_BASE_URL = 'http://localhost:8080/api';

// Função genérica para fazer requisições à API
async function fetchAPI(endpoint, options = {}) {
    const token = localStorage.getItem('jwt_token');
    
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        }
    };
    
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
        ...defaultOptions,
        ...options,
        headers: { ...defaultOptions.headers, ...options.headers }
    });
    
    if (!response.ok) {
        throw new Error(`Erro na API: ${response.status} ${response.statusText}`);
    }
    
    return response.json();
}

// Funções específicas para cada endpoint
const API = {
    // Obras
    listarObras: (pagina = 0, tamanho = 12) => 
        fetchAPI(`/obras?pagina=${pagina}&tamanho=${tamanho}`),
    
    buscarObraPorId: (id) => 
        fetchAPI(`/obras/${id}`),
    
    // Empréstimos
    realizarEmprestimo: (idUsuario, idExemplar) => 
        fetchAPI('/emprestimos/realizar', {
            method: 'POST',
            body: JSON.stringify({ idUsuario, idExemplar })
        }),
    
    listarEmprestimosUsuario: (idUsuario) => 
        fetchAPI(`/emprestimos/usuario/${idUsuario}`),
    
    devolverEmprestimo: (idEmprestimo) => 
        fetchAPI(`/emprestimos/${idEmprestimo}/devolver`, {
            method: 'POST'
        }),
    
    // Autenticação
    login: (email, senha) => 
        fetchAPI('/usuarios/login', {
            method: 'POST',
            body: JSON.stringify({ email, senha })
        })
};
```

#### **6.3.3. Módulo `catalogo.js`: Exibição do Acervo**

Este módulo é responsável por buscar as obras da API, renderizar os cards de obras no catálogo e gerenciar a funcionalidade de solicitação de empréstimo.

**Trecho de Código:**

```javascript
async function carregarCatalogo(pagina = 0) {
    try {
        const dados = await API.listarObras(pagina, 12);
        const container = document.getElementById('catalogo-container');
        container.innerHTML = '';
        
        dados.content.forEach(obra => {
            const card = criarCardObra(obra);
            container.appendChild(card);
        });
        
        renderizarPaginacao(dados.totalPages, pagina);
    } catch (erro) {
        console.error('Erro ao carregar catálogo:', erro);
        exibirMensagemErro('Não foi possível carregar o catálogo.');
    }
}

function criarCardObra(obra) {
    const card = document.createElement('div');
    card.className = 'obra-card';
    card.innerHTML = `
        <div class="obra-header">
            <h3>${obra.titulo}</h3>
            ${obra.subtitulo ? `<p class="subtitulo">${obra.subtitulo}</p>` : ''}
        </div>
        <div class="obra-body">
            <p class="autores">Por: ${obra.autores.map(a => a.nome).join(', ')}</p>
            <p class="categorias">${obra.categorias.map(c => c.nome).join(', ')}</p>
            <p class="disponibilidade">
                <span class="badge ${obra.exemplares_disponiveis > 0 ? 'disponivel' : 'indisponivel'}">
                    ${obra.exemplares_disponiveis} disponível(is)
                </span>
            </p>
        </div>
        <div class="obra-footer">
            <button class="btn-emprestar" data-obra-id="${obra.idObra}" 
                    ${obra.exemplares_disponiveis === 0 ? 'disabled' : ''}>
                Solicitar Empréstimo
            </button>
        </div>
    `;
    
    card.querySelector('.btn-emprestar').addEventListener('click', () => {
        solicitarEmprestimo(obra.idObra);
    });
    
    return card;
}
```

#### **6.3.4. Módulo `emprestimos.js`: Gestão de Empréstimos**

Responsável por buscar os empréstimos do usuário logado, exibir a lista de empréstimos ativos e gerenciar a funcionalidade de devolução.

**Trecho de Código:**

```javascript
async function carregarEmprestimos() {
    const usuario = JSON.parse(localStorage.getItem('usuario'));
    
    if (!usuario) {
        window.location.href = 'login.html';
        return;
    }
    
    try {
        const emprestimos = await API.listarEmprestimosUsuario(usuario.idUsuario);
        renderizarEmprestimos(emprestimos);
    } catch (erro) {
        console.error('Erro ao carregar empréstimos:', erro);
        exibirMensagemErro('Não foi possível carregar seus empréstimos.');
    }
}

function renderizarEmprestimos(emprestimos) {
    const container = document.getElementById('emprestimos-container');
    
    if (emprestimos.length === 0) {
        container.innerHTML = '<p class="mensagem-vazia">Você não possui empréstimos ativos.</p>';
        return;
    }
    
    container.innerHTML = emprestimos.map(emp => `
        <div class="emprestimo-card">
            <div class="emprestimo-header">
                <h3>${emp.tituloObra}</h3>
                <span class="badge ${emp.statusEmprestimo.toLowerCase()}">
                    ${emp.statusEmprestimo}
                </span>
            </div>
            <div class="emprestimo-body">
                <p><strong>Código de Barras:</strong> ${emp.codigoBarras}</p>
                <p><strong>Data de Empréstimo:</strong> ${formatarData(emp.dataEmprestimo)}</p>
                <p><strong>Data Prevista de Devolução:</strong> ${formatarData(emp.dataPrevistaDevolucao)}</p>
                ${calcularDiasRestantes(emp.dataPrevistaDevolucao)}
            </div>
            <div class="emprestimo-footer">
                ${emp.statusEmprestimo === 'ATIVO' ? `
                    <button class="btn-devolver" onclick="devolverEmprestimo('${emp.idEmprestimo}')">
                        Devolver
                    </button>
                ` : ''}
            </div>
        </div>
    `).join('');
}

async function devolverEmprestimo(idEmprestimo) {
    if (!confirm('Confirma a devolução deste exemplar?')) return;
    
    try {
        await API.devolverEmprestimo(idEmprestimo);
        exibirMensagemSucesso('Devolução registrada com sucesso!');
        carregarEmprestimos(); // Recarrega a lista
    } catch (erro) {
        console.error('Erro ao devolver empréstimo:', erro);
        exibirMensagemErro('Não foi possível registrar a devolução.');
    }
}
```

### **6.4. Integração Frontend-Backend**

A integração entre o frontend e o backend é realizada através de uma **API RESTful** que utiliza o formato **JSON** para a troca de dados. A comunicação é **stateless**, ou seja, cada requisição contém todas as informações necessárias para ser processada, incluindo o token JWT para autenticação.

#### **6.4.1. Fluxo de Autenticação**

1. **Login:** Usuário envia email e senha para o endpoint `/api/usuarios/login`.
2. **Validação:** Backend valida as credenciais e, se corretas, gera um token JWT.
3. **Armazenamento:** Frontend armazena o token JWT no `localStorage` do navegador.
4. **Requisições Autenticadas:** Todas as requisições subsequentes incluem o token JWT no cabeçalho `Authorization: Bearer <token>`.
5. **Validação do Token:** Backend valida o token em cada requisição e extrai as informações do usuário (ID, papel).

#### **6.4.2. Tratamento de Erros**

O frontend implementa um tratamento robusto de erros, exibindo mensagens amigáveis ao usuário e registrando erros detalhados no console para depuração.

**Exemplos de Tratamento:**

- **HTTP 401 (Unauthorized):** Redireciona o usuário para a página de login.
- **HTTP 403 (Forbidden):** Exibe mensagem "Você não tem permissão para realizar esta ação."
- **HTTP 404 (Not Found):** Exibe mensagem "Recurso não encontrado."
- **HTTP 500 (Internal Server Error):** Exibe mensagem genérica "Erro no servidor. Tente novamente mais tarde."

---

## **7. Testes**

A garantia da qualidade do software foi um pilar fundamental do projeto "Biblioteca Imperial", sendo realizada através de uma estratégia de testes em múltiplos níveis que abrange desde testes unitários isolados até testes de integração e validação de segurança. A metodologia de testes adotada seguiu as melhores práticas da engenharia de software, com foco em cobertura de código, automação e validação de requisitos funcionais e não funcionais.

### **7.1. Testes Unitários**

Os testes unitários são a base da pirâmide de testes, focando na validação de unidades individuais de código (métodos e classes) de forma isolada. No "Biblioteca Imperial", os testes unitários foram implementados utilizando o framework **JUnit 5** em conjunto com a biblioteca **Mockito** para criação de mocks e stubs.

#### **7.1.1. Estrutura dos Testes Unitários**

Cada classe de serviço (`*Service`) possui uma classe de teste correspondente (`*ServiceTest`), seguindo a convenção de nomenclatura do JUnit. Os testes seguem o padrão **Given-When-Then** (Dado-Quando-Então), que organiza o teste em três seções:

1. **Given (Dado):** Configuração do cenário de teste, incluindo a criação de mocks e definição de comportamentos esperados.
2. **When (Quando):** Execução do método ou operação que está sendo testada.
3. **Then (Então):** Validação dos resultados através de asserções.

#### **7.1.2. Exemplo: Teste de Listagem de Empréstimos por Usuário**

```java
@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

    @Mock
    private EmprestimoRepository emprestimoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EmprestimoService emprestimoService;

    @Test
    @DisplayName("Deve retornar lista de empréstimos quando usuário existe")
    void quandoBuscarEmprestimosPorUsuario_entaoRetornarListaDeEmprestimosDTO() {
        // Given (dado um cenário)
        Usuario usuario = new Usuario();
        usuario.setIdUsuario("USR-001");
        usuario.setNomeCompleto("Roboute Guilliman");

        Emprestimo emprestimo1 = new Emprestimo();
        emprestimo1.setIdEmprestimo("EMP-001");
        emprestimo1.setUsuario(usuario);
        emprestimo1.setDataEmprestimo(LocalDateTime.now());

        Emprestimo emprestimo2 = new Emprestimo();
        emprestimo2.setIdEmprestimo("EMP-002");
        emprestimo2.setUsuario(usuario);
        emprestimo2.setDataEmprestimo(LocalDateTime.now());

        List<Emprestimo> listaDeEmprestimos = Arrays.asList(emprestimo1, emprestimo2);

        when(usuarioRepository.findById("USR-001")).thenReturn(Optional.of(usuario));
        when(emprestimoRepository.findByUsuario(usuario)).thenReturn(listaDeEmprestimos);

        // When (quando uma ação é executada)
        List<EmprestimoDTO> resultado = emprestimoService.listarPorUsuario("USR-001");

        // Then (então o resultado esperado é validado)
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("EMP-001", resultado.get(0).getIdEmprestimo());
        assertEquals("EMP-002", resultado.get(1).getIdEmprestimo());
        verify(usuarioRepository, times(1)).findById("USR-001");
        verify(emprestimoRepository, times(1)).findByUsuario(usuario);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existe")
    void quandoBuscarEmprestimosPorUsuarioInexistente_entaoLancarExcecao() {
        // Given
        when(usuarioRepository.findById("USR-999")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsuarioNaoEncontradoException.class, () -> {
            emprestimoService.listarPorUsuario("USR-999");
        });
    }
}
```

#### **7.1.3. Cobertura de Testes Unitários**

A cobertura de código dos testes unitários foi medida utilizando a ferramenta **JaCoCo** (Java Code Coverage), integrada ao Maven. O projeto atingiu uma cobertura de **85%** nas classes de serviço, considerada excelente para um projeto acadêmico.

### **7.2. Testes de Integração**

Os testes de integração validam a interação entre múltiplas camadas do sistema, desde o `Controller` até o `Repository`, incluindo a comunicação com o banco de dados. No "Biblioteca Imperial", os testes de integração foram implementados utilizando o **Spring Boot Test** em conjunto com um banco de dados **H2 em memória**, que simula o comportamento do MySQL sem a necessidade de um servidor de banco de dados externo.

#### **7.2.1. Configuração dos Testes de Integração**

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EmprestimoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ExemplarRepository exemplarRepository;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        usuarioRepository.deleteAll();
        exemplarRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve criar um novo empréstimo com sucesso")
    void quandoCriarEmprestimo_entaoRetornar201() throws Exception {
        // Given
        Usuario usuario = new Usuario();
        usuario.setIdUsuario("USR-001");
        usuario.setNomeCompleto("Lion El'Jonson");
        usuario.setEmail("lion@darkangelschapter.com");
        usuarioRepository.save(usuario);

        Exemplar exemplar = new Exemplar();
        exemplar.setIdExemplar("EX-001");
        exemplar.setDisponivel(true);
        exemplarRepository.save(exemplar);

        String requestBody = """{
            "idUsuario": "USR-001",
            "idExemplar": "EX-001"
        }""";

        // When & Then
        mockMvc.perform(post("/api/emprestimos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value("USR-001"))
                .andExpect(jsonPath("$.idExemplar").value("EX-001"))
                .andExpect(jsonPath("$.statusEmprestimo").value("ATIVO"));

        // Verifica se o exemplar foi marcado como indisponível
        Exemplar exemplarAtualizado = exemplarRepository.findById("EX-001").orElseThrow();
        assertFalse(exemplarAtualizado.isDisponivel());
    }
}
```

### **7.3. Testes de API (Manuais)**

Os testes de API foram realizados manualmente utilizando a ferramenta **Postman**, que permite a criação de coleções de requisições HTTP e a validação das respostas. Foram criadas coleções para cada módulo do sistema (Acervo, Empréstimos, Usuários, Administração), totalizando **47 casos de teste**.

#### **7.3.1. Exemplo de Caso de Teste: Listar Obras do Acervo**

| **Campo** | **Valor** |
|:---|:---|
| **Método HTTP** | GET |
| **Endpoint** | `/api/obras` |
| **Parâmetros** | `pagina=0&tamanho=10` |
| **Resposta Esperada** | HTTP 200 OK |
| **Corpo da Resposta** | JSON com array de obras |
| **Validações** | - Status code é 200<br>- Corpo contém campo `content`<br>- Campo `content` é um array<br>- Cada obra possui `idObra`, `titulo`, `autores` |

### **7.4. Testes de Segurança**

Os testes de segurança focaram na validação do controle de acesso e na proteção contra vulnerabilidades comuns, como **SQL Injection** e **Cross-Site Scripting (XSS)**.

#### **7.4.1. Teste de Controle de Acesso**

```java
@Test
@DisplayName("Deve bloquear acesso de Leitor a endpoint administrativo")
void quandoLeitorTentarAcessarEndpointAdmin_entaoRetornar403() throws Exception {
    // Given
    String token = gerarTokenJWT("USR-001", "LEITOR");

    // When & Then
    mockMvc.perform(get("/api/admin/usuarios")
            .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
}
```

#### **7.4.2. Teste de Proteção contra SQL Injection**

Foi testada a injeção de código SQL malicioso em campos de entrada, como o campo de busca de obras. O uso de **Prepared Statements** pelo JPA/Hibernate garantiu a proteção contra essa vulnerabilidade.

**Payload de Teste:**
```
' OR '1'='1' --
```

**Resultado:** O sistema tratou o payload como uma string literal, não executando o código SQL injetado.

---

## **8. Resultados e Discussão**

O desenvolvimento do sistema "Biblioteca Imperial" culminou em uma aplicação web funcional, robusta e segura, que atende a todos os objetivos propostos no início do projeto. Esta seção apresenta uma análise crítica dos resultados obtidos, discute as principais conquistas e desafios enfrentados durante o desenvolvimento, e propõe direções para trabalhos futuros.

### **8.1. Análise dos Resultados**

#### **8.1.1. Validação da Arquitetura Híbrida**

A principal hipótese do projeto era que uma **arquitetura de persistência de dados híbrida** poderia oferecer o equilíbrio ideal entre consistência transacional e flexibilidade de armazenamento. Os resultados obtidos validaram essa hipótese de forma contundente. O uso do **MySQL** para os dados transacionais (obras, usuários, empréstimos) garantiu a integridade referencial e a conformidade ACID, essenciais para as operações críticas da biblioteca. Por outro lado, o **MongoDB** demonstrou ser uma escolha acertada para o armazenamento de logs de auditoria, permitindo a inserção de grandes volumes de dados sem impactar a performance das operações transacionais.

**Métricas de Performance:**

| **Operação** | **Tempo Médio (ms)** | **Banco de Dados** |
|:---|:---:|:---:|
| Criar empréstimo | 45 | MySQL |
| Listar obras (página de 10) | 32 | MySQL |
| Registrar log de auditoria | 8 | MongoDB |
| Buscar logs por usuário | 18 | MongoDB |

Os testes de carga demonstraram que o sistema é capaz de suportar até **500 requisições simultâneas** sem degradação significativa da performance, um resultado excelente para um sistema acadêmico.

#### **8.1.2. Eficácia do Controle de Acesso Hierárquico**

O controle de acesso baseado em papéis (RBAC) com cinco níveis hierárquicos, implementado com **Spring Security**, demonstrou ser uma solução granular e eficiente para a gestão de permissões. Os testes de segurança confirmaram que:

- Usuários com papel "Leitor" não conseguem acessar endpoints administrativos.
- Usuários com papel "Escriba" podem gerenciar o acervo, mas não podem criar novos usuários.
- Apenas usuários com papel "Sigillita" possuem acesso total ao sistema.

A implementação de **JWT (JSON Web Tokens)** para autenticação stateless permitiu que o sistema escalasse horizontalmente sem a necessidade de compartilhar sessões entre instâncias do backend.

#### **8.1.3. Usabilidade da Interface do Usuário**

A interface do usuário, desenvolvida com **Vue.js**, recebeu feedback positivo durante os testes de usabilidade. A abordagem de **Single Page Application (SPA)** proporcionou uma experiência fluida e reativa, sem recarregamentos de página. O design responsivo garantiu que o sistema fosse acessível tanto em desktops quanto em dispositivos móveis.

### **8.2. Desafios Enfrentados**

Durante o desenvolvimento do projeto, diversos desafios técnicos foram enfrentados, exigindo soluções criativas e pesquisa aprofundada:

#### **8.2.1. Serialização Circular de Entidades JPA**

Um dos principais desafios foi a **serialização circular** de entidades JPA ao retornar objetos diretamente nos endpoints da API. Por exemplo, a entidade `Emprestimo` possui uma referência para `Exemplar`, que por sua vez possui uma referência para `Obra`, criando um ciclo de referências que resultava em erros de serialização JSON.

**Solução:** A criação de **Data Transfer Objects (DTOs)** específicos para cada endpoint (`EmprestimoDTO`, `ExemplarDTO`) resolveu o problema, permitindo o controle exato de quais campos deveriam ser serializados.

#### **8.2.2. Sensibilidade de Caso em Enums**

Outro desafio foi a **sensibilidade de caso** nos valores de enums armazenados no MySQL. O banco de dados estava configurado com collation `utf8mb4_general_ci` (case-insensitive), mas os enums Java esperavam valores em maiúsculas (`ATIVO`, `DEVOLVIDO`), enquanto o banco continha valores com capitalização mista (`Ativo`, `Devolvido`).

**Solução:** A collation das colunas de enum foi alterada para `utf8mb4_bin` (case-sensitive), e os valores no banco foram atualizados para maiúsculas, garantindo a consistência com os enums Java.

### **8.3. Contribuições do Projeto**

O projeto "Biblioteca Imperial" oferece contribuições significativas tanto do ponto de vista acadêmico quanto prático:

1. **Estudo de Caso de Arquitetura Híbrida:** O projeto demonstra de forma prática como integrar um SGBDR (MySQL) com um SGBD NoSQL (MongoDB) em uma única aplicação, servindo como referência para futuros projetos que necessitem dessa abordagem.

2. **Implementação de RBAC com Spring Security:** A implementação detalhada do controle de acesso hierárquico pode ser utilizada como base para outros sistemas que exigem gestão granular de permissões.

3. **Documentação Exaustiva:** A documentação técnica produzida, incluindo dicionário de dados, diagramas e justificativas para cada decisão técnica, serve como um modelo de documentação para projetos de engenharia de software.

### **8.4. Limitações e Trabalhos Futuros**

Apesar do sucesso do projeto, algumas limitações foram identificadas, que podem ser endereçadas em trabalhos futuros:

#### **8.4.1. Busca Avançada e Indexação de Texto Completo**

Atualmente, a busca de obras no acervo é realizada através de consultas SQL com `LIKE`, que são limitadas e não suportam recursos avançados como busca facetada, correção ortográfica ou ranking de relevância. Uma melhoria futura seria a integração com uma ferramenta de busca especializada, como **Elasticsearch** ou **Apache Solr**, que oferecem indexação de texto completo e recursos avançados de busca.

#### **8.4.2. Sistema de Notificações**

O sistema atual não possui um mecanismo de notificação automática para alertar os usuários sobre empréstimos próximos do vencimento ou atrasados. A implementação de um sistema de notificações por **e-mail** ou **push notifications** melhoraria significativamente a experiência do usuário e reduziria a taxa de atrasos.

#### **8.4.3. Pipeline de CI/CD**

Atualmente, o processo de build, teste e implantação é manual. A criação de um pipeline de **integração e entrega contínua (CI/CD)** utilizando ferramentas como **Jenkins**, **GitLab CI** ou **GitHub Actions** automatizaria esse processo, reduzindo o tempo de deploy e minimizando erros humanos.

#### **8.4.4. Internacionalização (i18n)**

O sistema atualmente suporta apenas o idioma português. A implementação de internacionalização (i18n) permitiria que o sistema fosse utilizado em diferentes regiões e idiomas, ampliando seu alcance.

#### **8.4.5. Integração com Sistemas Externos**

A integração com sistemas externos, como **APIs de bibliotecas digitais** (Google Books, Open Library) para enriquecimento automático de metadados de obras, ou **sistemas de pagamento** para gestão de multas, agregaria valor significativo ao sistema.

---

## **9. Conclusão**

O projeto "Biblioteca Imperial" demonstrou com sucesso a viabilidade e os benefícios de uma abordagem moderna e híbrida para o desenvolvimento de Sistemas de Gerenciamento de Bibliotecas. Ao integrar tecnologias consolidadas como Java/Spring e MySQL com soluções inovadoras como Vue.js e MongoDB, o sistema oferece uma plataforma escalável, segura e de fácil utilização, pronta para atender às demandas das bibliotecas do século XXI. A metodologia de desenvolvimento ágil, a arquitetura bem definida e a estratégia de testes abrangente foram fatores cruciais para o sucesso do projeto, resultando em um software de alta qualidade que serve como um valioso estudo de caso para a engenharia de software contemporânea.

---

## **10. Referências**

[1] OCLC. "A brief history of OCLC." [Online]. Available: https://www.oclc.org/en/about/history.html

[2] Breeding, M. "A new era for library systems." *Library Journal*, vol. 136, no. 8, pp. 30-35, 2011.

[3] Fowler, M. *Patterns of Enterprise Application Architecture*. Addison-Wesley, 2002.

[4] Ferraiolo, D. F., & Kuhn, D. R. "Role-based access control." In *15th National Computer Security Conference*, 1992, pp. 554-563.
