/**
 * API.JS - Módulo de Integração com Backend
 * Biblioteca Imperial - Warhammer 40k
 * 
 * Autores:
 * - Samuel Telles de Vasconcellos Resende
 * - Rafael Machado dos Santos
 * - Raphael Ryan Pires Simão
 * - Yurik Alexsander Soares Feitosa
 */

// Configuração da API
const API_BASE_URL = 'http://localhost:8080/api';

/**
 * Classe para gerenciar requisições à API
 */
class BibliotecaAPI {
    constructor(baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Realiza requisição HTTP genérica
     */
    async request(endpoint, options = {}) {
        const url = `${this.baseUrl}${endpoint}`;
        
        // Obter credenciais do usuário logado
        const usuario = obterUsuarioLogado();
        
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
            },
        };
        
        // Adicionar Basic Auth se usuário estiver logado
        if (usuario && usuario.email && usuario.senha) {
            // Para Basic Auth, precisamos de email:senha em base64
            // ATENÇÃO: Salvar senha no localStorage é APENAS para fins acadêmicos!
            // Em produção, isso seria um JWT ou OAuth token
            const credentials = btoa(`${usuario.email}:${usuario.senha}`);
            defaultOptions.headers['Authorization'] = `Basic ${credentials}`;
        }

        const config = { ...defaultOptions, ...options };

        try {
            const response = await fetch(url, config);
            
            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.mensagem || `Erro HTTP: ${response.status}`);
            }

            // Se for 204 No Content, retorna null
            if (response.status === 204) {
                return null;
            }

            return await response.json();
        } catch (error) {
            console.error('Erro na requisição:', error);
            throw error;
        }
    }

    /**
     * GET request
     */
    async get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }

    /**
     * POST request
     */
    async post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data),
        });
    }

    /**
     * PUT request
     */
    async put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data),
        });
    }

    /**
     * DELETE request
     */
    async delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }

    // ========================================================================
    // ENDPOINTS DE AUTENTICAÇÃO
    // ========================================================================

    async login(email, senha) {
        const response = await this.post('/auth/login', { email, senha });
        
        // Salvar a senha no localStorage para usar em Basic Auth
        // ATENÇÃO: Isso é APENAS para fins acadêmicos!
        // Em produção, NUNCA salve senhas no localStorage!
        if (response && response.usuario) {
            response.usuario.senha = senha;
        }
        
        return response;
    }

    async validarSessao() {
        return this.get('/auth/validar');
    }

    // ========================================================================
    // ENDPOINTS DE OBRAS
    // ========================================================================

    async listarObras() {
        return this.get('/obras');
    }

    async buscarObraPorId(id) {
        return this.get(`/obras/${id}`);
    }

    async buscarObrasPorTitulo(titulo) {
        return this.get(`/obras/buscar?titulo=${encodeURIComponent(titulo)}`);
    }

    async buscarObrasPorCategoria(idCategoria) {
        return this.get(`/obras/categoria/${idCategoria}`);
    }

    async buscarObrasPorAutor(idAutor) {
        return this.get(`/obras/autor/${idAutor}`);
    }

    // ========================================================================
    // ENDPOINTS DE EMPRÉSTIMOS
    // ========================================================================

    async realizarEmprestimo(idExemplar, idUsuario, diasEmprestimo = 14) {
        return this.post('/emprestimos/realizar', {
            idExemplar,
            idUsuario,
            diasEmprestimo,
        });
    }

    async realizarDevolucao(idEmprestimo) {
        return this.put(`/emprestimos/${idEmprestimo}/devolver`);
    }

    async renovarEmprestimo(idEmprestimo, diasRenovacao = 7) {
        return this.put(`/emprestimos/${idEmprestimo}/renovar?diasRenovacao=${diasRenovacao}`);
    }

    async listarEmprestimosUsuario(idUsuario) {
        return this.get(`/emprestimos/usuario/${idUsuario}`);
    }

    async listarEmprestimosAtivos() {
        return this.get('/emprestimos/ativos');
    }

    async listarEmprestimosAtrasados() {
        return this.get('/emprestimos/atrasados');
    }

    // ========================================================================
    // ENDPOINTS DE USUÁRIOS
    // ========================================================================

    async listarUsuarios() {
        return this.get('/usuarios');
    }

    async buscarUsuarioPorId(id) {
        return this.get(`/usuarios/${id}`);
    }

    async buscarUsuarioPorEmail(email) {
        return this.get(`/usuarios/email/${encodeURIComponent(email)}`);
    }

    // ========================================================================
    // ENDPOINTS DE HISTÓRICO (MongoDB)
    // ========================================================================

    async registrarConsulta(historico) {
        return this.post('/historico-consultas', historico);
    }

    async buscarHistoricoUsuario(idUsuario) {
        return this.get(`/historico-consultas/usuario/${idUsuario}`);
    }

    async buscarConsultasRecentes(idUsuario) {
        return this.get(`/historico-consultas/usuario/${idUsuario}/recentes`);
    }
}

// Instância global da API
const api = new BibliotecaAPI(API_BASE_URL);

/**
 * Funções auxiliares de autenticação
 */
function salvarUsuarioLogado(usuario) {
    localStorage.setItem('usuarioLogado', JSON.stringify(usuario));
}

function obterUsuarioLogado() {
    const usuario = localStorage.getItem('usuarioLogado');
    return usuario ? JSON.parse(usuario) : null;
}

function limparSessao() {
    localStorage.removeItem('usuarioLogado');
}

function verificarAutenticacao() {
    const usuario = obterUsuarioLogado();
    if (!usuario) {
        window.location.href = 'index.html';
        return null;
    }
    return usuario;
}

function logout() {
    limparSessao();
    window.location.href = 'index.html';
}

/**
 * Funções auxiliares de formatação
 */
function formatarData(dataString) {
    if (!dataString) return 'N/A';
    const data = new Date(dataString);
    return data.toLocaleDateString('pt-BR');
}

function formatarDataHora(dataString) {
    if (!dataString) return 'N/A';
    const data = new Date(dataString);
    return data.toLocaleString('pt-BR');
}

function formatarMoeda(valor) {
    return new Intl.NumberFormat('pt-BR', {
        style: 'currency',
        currency: 'BRL',
    }).format(valor);
}

function calcularDiasRestantes(dataPrevisao) {
    if (!dataPrevisao) return null;
    const hoje = new Date();
    const previsao = new Date(dataPrevisao);
    const diff = Math.ceil((previsao - hoje) / (1000 * 60 * 60 * 24));
    return diff;
}

/**
 * Funções auxiliares de UI
 */
function mostrarLoading(elementId = 'loading') {
    const loading = document.getElementById(elementId);
    if (loading) loading.style.display = 'block';
}

function ocultarLoading(elementId = 'loading') {
    const loading = document.getElementById(elementId);
    if (loading) loading.style.display = 'none';
}

function mostrarErro(mensagem, containerId = 'errorContainer') {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="error-message">
                ⚠️ ${mensagem}
            </div>
        `;
        container.style.display = 'block';
    }
}

function ocultarErro(containerId = 'errorContainer') {
    const container = document.getElementById(containerId);
    if (container) {
        container.style.display = 'none';
        container.innerHTML = '';
    }
}

function mostrarSucesso(mensagem, containerId = 'errorContainer') {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = `
            <div class="success-message">
                ✓ ${mensagem}
            </div>
        `;
        container.style.display = 'block';
        
        // Remove a mensagem após 5 segundos
        setTimeout(() => {
            ocultarErro(containerId);
        }, 5000);
    }
}
