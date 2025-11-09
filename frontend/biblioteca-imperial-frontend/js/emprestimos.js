/**
 * EMPRESTIMOS.JS - Módulo de Gerenciamento de Empréstimos
 * Biblioteca Imperial - Warhammer 40k
 * 
 * Autores:
 * - Samuel Telles de Vasconcellos Resende
 * - Rafael Machado dos Santos
 * - Raphael Ryan Pires Simão
 * - Yurik Alexsander Soares Feitosa
 */

let emprestimosCache = [];

// Aguarda o carregamento do DOM
document.addEventListener('DOMContentLoaded', function() {
    // Verifica autenticação
    const usuario = verificarAutenticacao();
    if (!usuario) return;

    // Exibe nome do usuário
    document.getElementById('userName').textContent = usuario.nome;

    // Carrega os empréstimos
    carregarEmprestimos();
});

/**
 * Carrega os empréstimos do usuário logado
 */
async function carregarEmprestimos() {
    const usuario = obterUsuarioLogado();
    if (!usuario) return;

    mostrarLoading();
    ocultarErro();

    try {
        const emprestimos = await api.listarEmprestimosUsuario(usuario.id);
        emprestimosCache = emprestimos;
        
        renderizarEmprestimos(emprestimos);
        atualizarEstatisticas(emprestimos);
    } catch (error) {
        console.error('Erro ao carregar empréstimos:', error);
        mostrarErro('Erro ao carregar seus empréstimos. Tente novamente.');
    } finally {
        ocultarLoading();
    }
}

/**
 * Atualiza as estatísticas de empréstimos
 */
function atualizarEstatisticas(emprestimos) {
    const ativos = emprestimos.filter(e => e.statusEmprestimo === 'ATIVO');
    const atrasados = emprestimos.filter(e => e.statusEmprestimo === 'ATRASADO');
    
    // Calcula total de multas
    let totalMultas = 0;
    emprestimos.forEach(emp => {
        if (emp.multa && !emp.multa.paga) {
            totalMultas += emp.multa.valorMulta || 0;
        }
    });

    document.getElementById('totalAtivos').textContent = ativos.length;
    document.getElementById('totalAtrasados').textContent = atrasados.length;
    document.getElementById('totalMultas').textContent = formatarMoeda(totalMultas);
}

/**
 * Renderiza a lista de empréstimos
 */
function renderizarEmprestimos(emprestimos) {
    const container = document.getElementById('emprestimosContainer');
    const emptyState = document.getElementById('emptyState');

    // Filtra apenas empréstimos ativos ou atrasados
    const emprestimosAtivos = emprestimos.filter(e => 
        e.statusEmprestimo === 'ATIVO' || e.statusEmprestimo === 'ATRASADO'
    );

    if (!emprestimosAtivos || emprestimosAtivos.length === 0) {
        container.style.display = 'none';
        emptyState.style.display = 'block';
        return;
    }

    container.style.display = 'flex';
    emptyState.style.display = 'none';

    container.innerHTML = emprestimosAtivos.map(emp => {
        const diasRestantes = calcularDiasRestantes(emp.dataPrevistaDevolucao);
        const atrasado = emp.statusEmprestimo === 'ATRASADO';
        const diasAtraso = atrasado ? Math.abs(diasRestantes) : 0;

        return `
            <div class="emprestimo-card">
                <div class="emprestimo-info">
                    <h4>${emp.tituloObra || 'Obra não identificada'}</h4>
                    <p><strong>Exemplar:</strong> ${emp.codigoBarras || 'N/A'}</p>
                    <p><strong>Data de Empréstimo:</strong> ${formatarData(emp.dataEmprestimo)}</p>
                    <p><strong>Previsão de Devolução:</strong> ${formatarData(emp.dataPrevistaDevolucao)}</p>
                    
                    ${atrasado ? `
                        <p style="color: var(--danger-red); font-weight: bold;">
                            ⚠️ Atrasado há ${diasAtraso} dia(s)
                        </p>
                        ${emp.multa ? `
                            <p style="color: var(--warning-yellow);">
                                💰 Multa: ${formatarMoeda(emp.multa.valorMulta || 0)}
                            </p>
                        ` : ''}
                    ` : `
                        <p style="color: var(--success-green);">
                            ✓ ${diasRestantes > 0 ? `${diasRestantes} dia(s) restante(s)` : 'Vence hoje'}
                        </p>
                    `}
                    
                    ${emp.numeroRenovacoes > 0 ? `
                        <p><strong>Renovações:</strong> ${emp.numeroRenovacoes}/3</p>
                    ` : ''}
                </div>

                <div class="emprestimo-status">
                    <span class="status-badge ${atrasado ? 'status-atrasado' : 'status-ativo'}">
                        ${atrasado ? '⚠️ ATRASADO' : '✓ ATIVO'}
                    </span>
                </div>

                <div class="emprestimo-acoes" style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                    <button 
                        onclick="realizarDevolucao('${emp.idEmprestimo}')" 
                        class="btn-success"
                        style="padding: 0.5rem 1rem; font-size: 0.9rem;"
                    >
                        🔙 Devolver
                    </button>
                    
                    ${!atrasado && emp.numeroRenovacoes < 3 ? `
                        <button 
                            onclick="renovarEmprestimo('${emp.idEmprestimo}')" 
                            class="btn-secondary"
                            style="padding: 0.5rem 1rem; font-size: 0.9rem;"
                        >
                            🔄 Renovar
                        </button>
                    ` : ''}
                </div>
            </div>
        `;
    }).join('');
}

/**
 * Realiza a devolução de um empréstimo
 */
async function realizarDevolucao(idEmprestimo) {
    if (!confirm('Confirma a devolução deste empréstimo?')) {
        return;
    }

    mostrarLoading();
    ocultarErro();

    try {
        await api.realizarDevolucao(idEmprestimo);
        mostrarSucesso('Devolução realizada com sucesso!');
        
        // Recarrega a lista após 1 segundo
        setTimeout(() => {
            carregarEmprestimos();
        }, 1000);
    } catch (error) {
        console.error('Erro ao realizar devolução:', error);
        mostrarErro('Erro ao realizar devolução. Tente novamente.');
    } finally {
        ocultarLoading();
    }
}

/**
 * Renova um empréstimo
 */
async function renovarEmprestimo(idEmprestimo) {
    if (!confirm('Deseja renovar este empréstimo por mais 7 dias?')) {
        return;
    }

    mostrarLoading();
    ocultarErro();

    try {
        await api.renovarEmprestimo(idEmprestimo, 7);
        mostrarSucesso('Empréstimo renovado com sucesso!');
        
        // Recarrega a lista após 1 segundo
        setTimeout(() => {
            carregarEmprestimos();
        }, 1000);
    } catch (error) {
        console.error('Erro ao renovar empréstimo:', error);
        
        let mensagem = 'Erro ao renovar empréstimo.';
        if (error.message.includes('limite')) {
            mensagem = 'Você atingiu o limite de 3 renovações para este empréstimo.';
        } else if (error.message.includes('atrasado')) {
            mensagem = 'Não é possível renovar um empréstimo atrasado.';
        }
        
        mostrarErro(mensagem);
    } finally {
        ocultarLoading();
    }
}
