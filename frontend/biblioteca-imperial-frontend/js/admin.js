/**
 * Admin.js - Funcionalidades Administrativas
 * Biblioteca Imperial - Warhammer 40k
 * 
 * Autores:
 * - Samuel Telles de Vasconcellos Resende
 * - Rafael Machado dos Santos
 * - Raphael Ryan Pires Simão
 * - Yurik Alexsander Soares Feitosa
 */

// Verificar autenticação e nível de acesso
document.addEventListener('DOMContentLoaded', async () => {
    const user = verificarAutenticacao();
    
    if (!user) {
        window.location.href = 'index.html';
        return;
    }
    
    // Exibir nome do usuário
    document.getElementById('userName').textContent = user.nome || user.nomeCompleto;
    
    // Exibir nível de acesso
    const nivelAcesso = user.nivelAcesso || user.grupo?.nivelAcesso || 1;
    const nomeGrupo = user.grupo || user.grupo?.nomeGrupo || 'Desconhecido';
    
    const badge = document.getElementById('accessLevelBadge');
    badge.innerHTML = `
        <span class="nivel-badge nivel-${nivelAcesso}">
            Nível ${nivelAcesso} - ${nomeGrupo}
        </span>
    `;
    
    // Verificar permissões
    if (nivelAcesso < 3) {
        mostrarErro('Acesso negado. Apenas Escribas Imperiais (Nível 3+) podem acessar esta área.');
        setTimeout(() => {
            window.location.href = 'catalogo.html';
        }, 3000);
        return;
    }
    
    // Carregar dados iniciais
    await carregarDadosIniciais();
    
    // Carregar usuários na tab ativa
    await carregarUsuarios();
});

// ============================================================================
// GERENCIAMENTO DE TABS
// ============================================================================

function showTab(tabName) {
    // Remover active de todas as tabs
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    
    // Ativar tab selecionada
    event.target.classList.add('active');
    document.getElementById(`tab-${tabName}`).classList.add('active');
    
    // Carregar dados da tab
    switch(tabName) {
        case 'usuarios':
            carregarUsuarios();
            break;
        case 'autores':
            carregarAutores();
            break;
        case 'categorias':
            carregarCategorias();
            break;
        case 'obras':
            carregarObras();
            controlarAcessoObras();
            break;
        case 'relatorios':
            carregarRelatorios();
            break;
    }
}

// ============================================================================
// CARREGAR DADOS INICIAIS
// ============================================================================

async function carregarDadosIniciais() {
    try {
        // Grupos fixos (dados estáticos)
        const grupos = [
            { idGrupo: 'GRP-00001', nomeGrupo: 'Senhores da Biblioteca', nivelAcesso: 5 },
            { idGrupo: 'GRP-00002', nomeGrupo: 'Lexicanum', nivelAcesso: 4 },
            { idGrupo: 'GRP-00003', nomeGrupo: 'Escribas Imperiais', nivelAcesso: 3 },
            { idGrupo: 'GRP-00004', nomeGrupo: 'Scholam Progenium', nivelAcesso: 2 },
            { idGrupo: 'GRP-00005', nomeGrupo: 'Servos do Conhecimento', nivelAcesso: 1 }
        ];
        
        const selectGrupo = document.getElementById('userGrupo');
        
        selectGrupo.innerHTML = '<option value="">Selecione...</option>';
        grupos.forEach(grupo => {
            const option = document.createElement('option');
            option.value = grupo.idGrupo;
            option.textContent = `${grupo.nomeGrupo} (Nível ${grupo.nivelAcesso})`;
            selectGrupo.appendChild(option);
        });
    } catch (error) {
        console.error('Erro ao carregar dados iniciais:', error);
    }
}

// ============================================================================
// GESTÃO DE USUÁRIOS
// ============================================================================

async function carregarUsuarios() {
    const tbody = document.getElementById('usuariosTableBody');
    tbody.innerHTML = '<tr><td colspan="6" class="loading">Carregando...</td></tr>';
    
    try {
        const usuarios = await api.get('/usuarios');
        
        if (usuarios.length === 0) {
            tbody.innerHTML = '<tr><td colspan="6" class="empty">Nenhum usuário encontrado</td></tr>';
            return;
        }
        
        tbody.innerHTML = usuarios.map(user => `
            <tr>
                <td>${user.nomeCompleto}</td>
                <td>${user.email}</td>
                <td>${user.grupo?.nomeGrupo || '-'}</td>
                <td>
                    <span class="nivel-badge nivel-${user.grupo?.nivelAcesso || 1}">
                        Nível ${user.grupo?.nivelAcesso || 1}
                    </span>
                </td>
                <td>
                    <span class="status-badge ${user.ativo ? 'status-ativo' : 'status-inativo'}">
                        ${user.ativo ? '✓ Ativo' : '✗ Inativo'}
                    </span>
                </td>
                <td class="actions">
                    <button class="btn-icon" onclick="editarUsuario('${user.idUsuario}')" title="Editar">
                        ✏️
                    </button>
                    <button class="btn-icon" onclick="toggleUsuarioStatus('${user.idUsuario}', ${!user.ativo})" title="${user.ativo ? 'Desativar' : 'Ativar'}">
                        ${user.ativo ? '🔒' : '🔓'}
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar usuários:', error);
        tbody.innerHTML = '<tr><td colspan="6" class="error">Erro ao carregar usuários</td></tr>';
    }
}

function showAddUserModal() {
    document.getElementById('userModalTitle').textContent = 'Novo Usuário';
    document.getElementById('userForm').reset();
    document.getElementById('userId').value = '';
    document.getElementById('userModal').classList.add('show');
}

function closeUserModal() {
    document.getElementById('userModal').classList.remove('show');
}

async function editarUsuario(idUsuario) {
    try {
        const user = await api.get(`/usuarios/${idUsuario}`);
        
        document.getElementById('userModalTitle').textContent = 'Editar Usuário';
        document.getElementById('userId').value = user.idUsuario;
        document.getElementById('userName').value = user.nomeCompleto;
        document.getElementById('userEmail').value = user.email;
        document.getElementById('userGrupo').value = user.idGrupo;
        document.getElementById('userAtivo').checked = user.ativo;
        document.getElementById('userPassword').required = false;
        
        document.getElementById('userModal').classList.add('show');
    } catch (error) {
        mostrarErro('Erro ao carregar dados do usuário');
    }
}

document.getElementById('userForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const userId = document.getElementById('userId').value;
    const dados = {
        nomeCompleto: document.getElementById('userName').value,
        email: document.getElementById('userEmail').value,
        idGrupo: document.getElementById('userGrupo').value,
        ativo: document.getElementById('userAtivo').checked
    };
    
    const senha = document.getElementById('userPassword').value;
    if (senha) {
        dados.senha = senha;
    }
    
    try {
        if (userId) {
            await api.put(`/api/usuarios/${userId}`, dados);
            mostrarSucesso('Usuário atualizado com sucesso!');
        } else {
            await api.post('/usuarios', dados);
            mostrarSucesso('Usuário criado com sucesso!');
        }
        
        closeUserModal();
        await carregarUsuarios();
    } catch (error) {
        mostrarErro('Erro ao salvar usuário: ' + (error.message || 'Erro desconhecido'));
    }
});

async function toggleUsuarioStatus(idUsuario, novoStatus) {
    if (!confirm(`Deseja realmente ${novoStatus ? 'ativar' : 'desativar'} este usuário?`)) {
        return;
    }
    
    try {
        await api.patch(`/api/usuarios/${idUsuario}/status`, { ativo: novoStatus });
        mostrarSucesso('Status atualizado com sucesso!');
        await carregarUsuarios();
    } catch (error) {
        mostrarErro('Erro ao atualizar status do usuário');
    }
}

// ============================================================================
// GESTÃO DE AUTORES
// ============================================================================

async function carregarAutores() {
    const tbody = document.getElementById('autoresTableBody');
    tbody.innerHTML = '<tr><td colspan="4" class="loading">Carregando...</td></tr>';
    
    try {
        const autores = await api.get('/autores');
        
        if (autores.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="empty">Nenhum autor encontrado</td></tr>';
            return;
        }
        
        tbody.innerHTML = autores.map(autor => `
            <tr>
                <td>${autor.nomeAutor}</td>
                <td>${autor.nacionalidade || '-'}</td>
                <td>${autor.totalObras || 0} obras</td>
                <td class="actions">
                    <button class="btn-icon" onclick="editarAutor('${autor.idAutor}')" title="Editar">
                        ✏️
                    </button>
                    <button class="btn-icon" onclick="excluirAutor('${autor.idAutor}')" title="Excluir">
                        🗑️
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar autores:', error);
        tbody.innerHTML = '<tr><td colspan="4" class="error">Erro ao carregar autores</td></tr>';
    }
}

function showAddAutorModal() {
    document.getElementById('autorModalTitle').textContent = 'Novo Autor';
    document.getElementById('autorForm').reset();
    document.getElementById('autorId').value = '';
    document.getElementById('autorModal').classList.add('show');
}

function closeAutorModal() {
    document.getElementById('autorModal').classList.remove('show');
}

async function editarAutor(idAutor) {
    try {
        const autor = await api.get(`/autores/${idAutor}`);
        
        document.getElementById('autorModalTitle').textContent = 'Editar Autor';
        document.getElementById('autorId').value = autor.idAutor;
        document.getElementById('autorNome').value = autor.nomeAutor;
        document.getElementById('autorBiografia').value = autor.biografia || '';
        document.getElementById('autorNacionalidade').value = autor.nacionalidade || '';
        
        document.getElementById('autorModal').classList.add('show');
    } catch (error) {
        mostrarErro('Erro ao carregar dados do autor');
    }
}

document.getElementById('autorForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const autorId = document.getElementById('autorId').value;
    const dados = {
        nomeAutor: document.getElementById('autorNome').value,
        biografia: document.getElementById('autorBiografia').value,
        nacionalidade: document.getElementById('autorNacionalidade').value
    };
    
    try {
        if (autorId) {
            await api.put(`/api/autores/${autorId}`, dados);
            mostrarSucesso('Autor atualizado com sucesso!');
        } else {
            await api.post('/autores', dados);
            mostrarSucesso('Autor criado com sucesso!');
        }
        
        closeAutorModal();
        await carregarAutores();
    } catch (error) {
        mostrarErro('Erro ao salvar autor: ' + (error.message || 'Erro desconhecido'));
    }
});

async function excluirAutor(idAutor) {
    if (!confirm('Deseja realmente excluir este autor? Esta ação não pode ser desfeita.')) {
        return;
    }
    
    try {
        await api.delete(`/api/autores/${idAutor}`);
        mostrarSucesso('Autor excluído com sucesso!');
        await carregarAutores();
    } catch (error) {
        mostrarErro('Erro ao excluir autor. Verifique se não há obras vinculadas.');
    }
}

// ============================================================================
// GESTÃO DE CATEGORIAS
// ============================================================================

async function carregarCategorias() {
    const tbody = document.getElementById('categoriasTableBody');
    tbody.innerHTML = '<tr><td colspan="5" class="loading">Carregando...</td></tr>';
    
    try {
        const categorias = await api.get('/categorias');
        
        if (categorias.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty">Nenhuma categoria encontrada</td></tr>';
            return;
        }
        
        tbody.innerHTML = categorias.map(cat => `
            <tr>
                <td>${cat.nomeCategoria}</td>
                <td>${cat.descricao || '-'}</td>
                <td>
                    <span class="nivel-badge nivel-${cat.nivelRestricao}">
                        Nível ${cat.nivelRestricao}
                    </span>
                </td>
                <td>${cat.totalObras || 0} obras</td>
                <td class="actions">
                    <button class="btn-icon" onclick="editarCategoria('${cat.idCategoria}')" title="Editar">
                        ✏️
                    </button>
                    <button class="btn-icon" onclick="excluirCategoria('${cat.idCategoria}')" title="Excluir">
                        🗑️
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (error) {
        console.error('Erro ao carregar categorias:', error);
        tbody.innerHTML = '<tr><td colspan="5" class="error">Erro ao carregar categorias</td></tr>';
    }
}

function showAddCategoriaModal() {
    document.getElementById('categoriaModalTitle').textContent = 'Nova Categoria';
    document.getElementById('categoriaForm').reset();
    document.getElementById('categoriaId').value = '';
    document.getElementById('categoriaModal').classList.add('show');
}

function closeCategoriaModal() {
    document.getElementById('categoriaModal').classList.remove('show');
}

async function editarCategoria(idCategoria) {
    try {
        const categoria = await api.get(`/categorias/${idCategoria}`);
        
        document.getElementById('categoriaModalTitle').textContent = 'Editar Categoria';
        document.getElementById('categoriaId').value = categoria.idCategoria;
        document.getElementById('categoriaNome').value = categoria.nomeCategoria;
        document.getElementById('categoriaDescricao').value = categoria.descricao || '';
        document.getElementById('categoriaNivelRestricao').value = categoria.nivelRestricao;
        
        document.getElementById('categoriaModal').classList.add('show');
    } catch (error) {
        mostrarErro('Erro ao carregar dados da categoria');
    }
}

document.getElementById('categoriaForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const categoriaId = document.getElementById('categoriaId').value;
    const dados = {
        nomeCategoria: document.getElementById('categoriaNome').value,
        descricao: document.getElementById('categoriaDescricao').value,
        nivelRestricao: parseInt(document.getElementById('categoriaNivelRestricao').value)
    };
    
    try {
        if (categoriaId) {
            await api.put(`/api/categorias/${categoriaId}`, dados);
            mostrarSucesso('Categoria atualizada com sucesso!');
        } else {
            await api.post('/categorias', dados);
            mostrarSucesso('Categoria criada com sucesso!');
        }
        
        closeCategoriaModal();
        await carregarCategorias();
    } catch (error) {
        mostrarErro('Erro ao salvar categoria: ' + (error.message || 'Erro desconhecido'));
    }
});

async function excluirCategoria(idCategoria) {
    if (!confirm('Deseja realmente excluir esta categoria? Esta ação não pode ser desfeita.')) {
        return;
    }
    
    try {
        await api.delete(`/api/categorias/${idCategoria}`);
        mostrarSucesso('Categoria excluída com sucesso!');
        await carregarCategorias();
    } catch (error) {
        mostrarErro('Erro ao excluir categoria. Verifique se não há obras vinculadas.');
    }
}

// ============================================================================
// RELATÓRIOS E ESTATÍSTICAS
// ============================================================================

async function carregarRelatorios() {
    try {
        // Carregar estatísticas
        const stats = await api.get('/relatorios/estatisticas');
        
        document.getElementById('totalUsuarios').textContent = stats.totalUsuarios || 0;
        document.getElementById('totalObras').textContent = stats.totalObras || 0;
        document.getElementById('totalEmprestimos').textContent = stats.totalEmprestimosAtivos || 0;
        document.getElementById('totalAtrasados').textContent = stats.totalAtrasados || 0;
    } catch (error) {
        console.error('Erro ao carregar relatórios:', error);
        mostrarErro('Erro ao carregar estatísticas');
    }
}

// ============================================================================
// FUNÇÕES AUXILIARES
// ============================================================================

function mostrarSucesso(mensagem) {
    // TODO: Implementar toast/notificação
    alert(mensagem);
}

function mostrarErro(mensagem) {
    // TODO: Implementar toast/notificação
    alert(mensagem);
}

// Fechar modais ao clicar fora
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('show');
    }
}


// ============================================================================
// GESTÃO DE OBRAS
// ============================================================================

/**
 * Carregar obras
 */
async function carregarObras() {
    try {
        const obras = await api.get('/obras');
        const tbody = document.getElementById('obrasTableBody');
        
        if (!obras || obras.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="empty">Nenhuma obra cadastrada</td></tr>';
            return;
        }
        
        tbody.innerHTML = obras.map(obra => `
            <tr>
                <td><strong>${obra.titulo}</strong>${obra.subtitulo ? `<br><small>${obra.subtitulo}</small>` : ''}</td>
                <td>${obra.autores?.map(a => a.nomeAutor).join(', ') || '-'}</td>
                <td>${obra.categoria?.nomeCategoria || '-'}</td>
                <td>${obra.isbn || '-'}</td>
                <td>${obra.anoPublicacao || '-'}</td>
                <td>${obra.totalExemplares || 0} exemplar(es)</td>
                <td>
                    <button class="btn-icon" onclick="editarObra('${obra.idObra}')" title="Editar">
                        ✏️
                    </button>
                    <button class="btn-icon" onclick="excluirObra('${obra.idObra}')" title="Excluir">
                        🗑️
                    </button>
                </td>
            </tr>
        `).join('');
        
    } catch (error) {
        console.error('Erro ao carregar obras:', error);
        document.getElementById('obrasTableBody').innerHTML = 
            '<tr><td colspan="7" class="error">Erro ao carregar obras</td></tr>';
    }
}

/**
 * Mostrar modal de adicionar obra
 */
async function showAddObraModal() {
    document.getElementById('obraModalTitle').textContent = 'Nova Obra';
    document.getElementById('obraForm').reset();
    document.getElementById('obraId').value = '';
    
    // Carregar categorias no select
    await carregarCategoriasSelect();
    
    document.getElementById('obraModal').style.display = 'flex';
}

/**
 * Fechar modal de obra
 */
function closeObraModal() {
    document.getElementById('obraModal').style.display = 'none';
}

/**
 * Carregar categorias no select
 */
async function carregarCategoriasSelect() {
    try {
        const categorias = await api.get('/categorias');
        const select = document.getElementById('obraCategoria');
        
        select.innerHTML = '<option value="">Selecione...</option>' + 
            categorias.map(cat => `
                <option value="${cat.idCategoria}">${cat.nomeCategoria}</option>
            `).join('');
            
    } catch (error) {
        console.error('Erro ao carregar categorias:', error);
    }
}

/**
 * Salvar obra (criar ou atualizar)
 */
document.getElementById('obraForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const obraId = document.getElementById('obraId').value;
    const obra = {
        titulo: document.getElementById('obraTitulo').value,
        subtitulo: document.getElementById('obraSubtitulo').value || null,
        isbn: document.getElementById('obraIsbn').value,
        anoPublicacao: parseInt(document.getElementById('obraAno').value),
        idCategoria: document.getElementById('obraCategoria').value,
        editora: document.getElementById('obraEditora').value || null,
        idioma: document.getElementById('obraIdioma').value || null,
        numPaginas: parseInt(document.getElementById('obraPaginas').value) || null,
        sinopse: document.getElementById('obraSinopse').value || null,
        localizacaoFisica: document.getElementById('obraLocalizacao').value || null
    };
    
    try {
        if (obraId) {
            await api.put(`/obras/${obraId}`, obra);
            mostrarSucesso('Obra atualizada com sucesso!');
        } else {
            await api.post('/obras', obra);
            mostrarSucesso('Obra cadastrada com sucesso!');
        }
        
        closeObraModal();
        await carregarObras();
        
    } catch (error) {
        console.error('Erro ao salvar obra:', error);
        mostrarErro('Erro ao salvar obra: ' + error.message);
    }
});

/**
 * Editar obra
 */
async function editarObra(idObra) {
    try {
        const obra = await api.get(`/obras/${idObra}`);
        
        document.getElementById('obraModalTitle').textContent = 'Editar Obra';
        document.getElementById('obraId').value = obra.idObra;
        document.getElementById('obraTitulo').value = obra.titulo;
        document.getElementById('obraSubtitulo').value = obra.subtitulo || '';
        document.getElementById('obraIsbn').value = obra.isbn || '';
        document.getElementById('obraAno').value = obra.anoPublicacao || '';
        document.getElementById('obraEditora').value = obra.editora || '';
        document.getElementById('obraIdioma').value = obra.idioma || '';
        document.getElementById('obraPaginas').value = obra.numPaginas || '';
        document.getElementById('obraSinopse').value = obra.sinopse || '';
        document.getElementById('obraLocalizacao').value = obra.localizacaoFisica || '';
        
        await carregarCategoriasSelect();
        document.getElementById('obraCategoria').value = obra.idCategoria || '';
        
        document.getElementById('obraModal').style.display = 'flex';
        
    } catch (error) {
        console.error('Erro ao carregar obra:', error);
        mostrarErro('Erro ao carregar dados da obra');
    }
}

/**
 * Excluir obra
 */
async function excluirObra(idObra) {
    if (!confirm('Tem certeza que deseja excluir esta obra? Esta ação não pode ser desfeita.')) {
        return;
    }
    
    try {
        await api.delete(`/obras/${idObra}`);
        mostrarSucesso('Obra excluída com sucesso!');
        await carregarObras();
        
    } catch (error) {
        console.error('Erro ao excluir obra:', error);
        mostrarErro('Erro ao excluir obra: ' + error.message);
    }
}

/**
 * Controlar visibilidade do botão "Nova Obra" baseado no nível de acesso
 */
function controlarAcessoObras() {
    const user = obterUsuarioLogado();
    const nivelAcesso = user?.nivelAcesso || user?.grupo?.nivelAcesso || 1;
    
    // Apenas níveis 4 e 5 podem adicionar obras
    if (nivelAcesso >= 4) {
        document.getElementById('btnNovaObra').style.display = 'inline-block';
    } else {
        document.getElementById('btnNovaObra').style.display = 'none';
    }
}

// Adicionar controle de acesso ao carregar a página
document.addEventListener('DOMContentLoaded', () => {
    controlarAcessoObras();
});
