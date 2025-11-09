/**
 * CATALOGO.JS - Módulo do Catálogo de Obras
 * Biblioteca Imperial - Warhammer 40k
 * 
 * Autores:
 * - Samuel Telles de Vasconcellos Resende
 * - Rafael Machado dos Santos
 * - Raphael Ryan Pires Simão
 * - Yurik Alexsander Soares Feitosa
 */

let obrasCache = [];
let obraAtual = null;

// Aguarda o carregamento do DOM
document.addEventListener('DOMContentLoaded', function() {
    // Verifica autenticação
    const usuario = verificarAutenticacao();
    if (!usuario) return;

    // Exibe nome do usuário
    document.getElementById('userName').textContent = usuario.nome || usuario.nomeCompleto;
    
    // Mostrar link de administração se nível >= 3
    const nivelAcesso = usuario.nivelAcesso || usuario.grupo?.nivelAcesso || 1;
    if (nivelAcesso >= 3) {
        const adminLink = document.getElementById('adminLink');
        if (adminLink) {
            adminLink.style.display = 'inline-block';
        }
    }

    // Carrega as obras
    carregarObras();
});

/**
 * Carrega todas as obras do catálogo
 */
async function carregarObras() {
    mostrarLoading();
    ocultarErro();

    try {
        const obras = await api.listarObras();
        obrasCache = obras;
        renderizarObras(obras);
    } catch (error) {
        console.error('Erro ao carregar obras:', error);
        mostrarErro('Erro ao carregar o catálogo de obras. Verifique sua conexão.');
    } finally {
        ocultarLoading();
    }
}

/**
 * Renderiza as obras no grid
 */
function renderizarObras(obras) {
    const container = document.getElementById('obrasContainer');
    
    if (!obras || obras.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">📚</div>
                <h3>Nenhuma obra encontrada</h3>
                <p>O catálogo está vazio ou não há resultados para sua busca.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = obras.map(obra => `
        <div class="obra-card" onclick="abrirDetalhesObra('${obra.idObra}')">
            <h3>${obra.titulo}</h3>
            <p><strong>Categoria:</strong> ${obra.categoria?.nomeCategoria || 'N/A'}</p>
            <p><strong>ISBN:</strong> ${obra.isbn || 'N/A'}</p>
            <p><strong>Ano:</strong> ${obra.anoPublicacao || 'N/A'}</p>
            <p class="obra-descricao">${obra.sinopse ? obra.sinopse.substring(0, 100) + '...' : 'Sem descrição'}</p>
            <span class="obra-badge">${obra.totalExemplares && obra.totalExemplares > 0 ? '✓ Disponível' : '✗ Indisponível'}</span>
        </div>
    `).join('');
}

/**
 * Busca obras por título
 */
async function buscarObras() {
    const termo = document.getElementById('searchInput').value.trim();

    if (!termo) {
        renderizarObras(obrasCache);
        return;
    }

    mostrarLoading();
    ocultarErro();

    try {
        const usuario = obterUsuarioLogado();
        
        // Busca as obras
        const obras = await api.buscarObrasPorTitulo(termo);
        renderizarObras(obras);

        // Registra a busca no histórico (MongoDB)
        if (usuario) {
            try {
                await api.registrarConsulta({
                    idUsuario: usuario.id,
                    nomeUsuario: usuario.nome,
                    emailUsuario: usuario.email,
                    tipoConsulta: 'OBRA',
                    termoBusca: termo,
                    resultadosIds: obras.map(o => o.idObra),
                    ipOrigem: 'frontend',
                });
            } catch (err) {
                console.warn('Erro ao registrar consulta:', err);
            }
        }
    } catch (error) {
        console.error('Erro ao buscar obras:', error);
        mostrarErro('Erro ao buscar obras. Tente novamente.');
    } finally {
        ocultarLoading();
    }
}

/**
 * Abre modal com detalhes da obra
 */
async function abrirDetalhesObra(idObra) {
    mostrarLoading();

    try {
        const obra = await api.buscarObraPorId(idObra);
        obraAtual = obra;
        
        const modal = document.getElementById('obraModal');
        const detalhes = document.getElementById('obraDetalhes');

        detalhes.innerHTML = `
            <h2 style="color: var(--imperial-gold); margin-bottom: 1rem;">
                ${obra.titulo}
            </h2>
            
            <div style="margin-bottom: 1rem;">
                <p><strong>ISBN:</strong> ${obra.isbn || 'N/A'}</p>
                <p><strong>Categoria:</strong> ${obra.categoria?.nomeCategoria || 'N/A'}</p>
                <p><strong>Ano de Publicação:</strong> ${obra.anoPublicacao || 'N/A'}</p>
                <p><strong>Editora:</strong> ${obra.editora || 'N/A'}</p>
                <p><strong>Idioma:</strong> ${obra.idioma || 'N/A'}</p>
                <p><strong>Páginas:</strong> ${obra.numeroPaginas || 'N/A'}</p>
            </div>

            ${obra.sinopse ? `
                <div style="margin-bottom: 1rem;">
                    <h3 style="color: var(--imperial-gold); font-size: 1.1rem; margin-bottom: 0.5rem;">
                        Sinopse
                    </h3>
                    <p style="text-align: justify; line-height: 1.6;">
                        ${obra.sinopse}
                    </p>
                </div>
            ` : ''}

            ${obra.autores && obra.autores.length > 0 ? `
                <div style="margin-bottom: 1rem;">
                    <h3 style="color: var(--imperial-gold); font-size: 1.1rem; margin-bottom: 0.5rem;">
                        Autores
                    </h3>
                    <p>${obra.autores.map(a => a.nomeCompleto).join(', ')}</p>
                </div>
            ` : ''}

            <div style="margin-top: 1.5rem; display: flex; gap: 1rem; flex-wrap: wrap;">
                <button onclick="solicitarEmprestimo('${obra.idObra}')" class="btn-primary">
                    📖 Solicitar Empréstimo
                </button>
                <button onclick="fecharModal()" class="btn-secondary">
                    Fechar
                </button>
            </div>
        `;

        modal.style.display = 'flex';
    } catch (error) {
        console.error('Erro ao carregar detalhes:', error);
        mostrarErro('Erro ao carregar detalhes da obra.');
    } finally {
        ocultarLoading();
    }
}

/**
 * Fecha o modal de detalhes
 */
function fecharModal() {
    const modal = document.getElementById('obraModal');
    modal.style.display = 'none';
    obraAtual = null;
}

/**
 * Solicita empréstimo de uma obra
 */
async function solicitarEmprestimo(idObra) {
    const usuario = obterUsuarioLogado();
    if (!usuario) {
        mostrarErro('Usuário não autenticado.');
        return;
    }

    // Busca um exemplar disponível da obra
    mostrarLoading();

    try {
        // Busca exemplares disponíveis via API
        const exemplares = await api.get(`/exemplares/obra/${idObra}/disponiveis`);
        
        if (!exemplares || exemplares.length === 0) {
            mostrarErro('Não há exemplares disponíveis para esta obra no momento.');
            return;
        }

        // Pega o primeiro exemplar disponível
        const exemplarDisponivel = exemplares[0];

        // Realiza o empréstimo
        const emprestimo = await api.realizarEmprestimo(
            exemplarDisponivel.idExemplar,
            usuario.id,
            14 // 14 dias de empréstimo
        );

        mostrarSucesso('Empréstimo realizado com sucesso!');
        fecharModal();

        // Redireciona para a página de empréstimos após 2 segundos
        setTimeout(() => {
            window.location.href = 'emprestimos.html';
        }, 2000);

    } catch (error) {
        console.error('Erro ao realizar empréstimo:', error);
        
        let mensagem = 'Erro ao realizar empréstimo.';
        if (error.message.includes('pendentes')) {
            mensagem = 'Você possui pendências (multas ou empréstimos atrasados).';
        } else if (error.message.includes('limite')) {
            mensagem = 'Você atingiu o limite de empréstimos simultâneos.';
        }
        
        mostrarErro(mensagem);
    } finally {
        ocultarLoading();
    }
}

// Fecha o modal ao clicar fora dele
window.onclick = function(event) {
    const modal = document.getElementById('obraModal');
    if (event.target === modal) {
        fecharModal();
    }
}
