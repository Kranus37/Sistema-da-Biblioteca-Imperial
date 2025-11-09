/**
 * AUTH.JS - Módulo de Autenticação
 * Biblioteca Imperial - Warhammer 40k
 * 
 * Autores:
 * - Samuel Telles de Vasconcellos Resende
 * - Rafael Machado dos Santos
 * - Raphael Ryan Pires Simão
 * - Yurik Alexsander Soares Feitosa
 */

// Aguarda o carregamento do DOM
document.addEventListener('DOMContentLoaded', function() {
    // Verifica se já está logado
    const usuarioLogado = obterUsuarioLogado();
    if (usuarioLogado) {
        // Se estiver na página de login, redireciona para o catálogo
        if (window.location.pathname.endsWith('index.html') || window.location.pathname === '/') {
            window.location.href = 'catalogo.html';
            return;
        }
    }

    // Configura o formulário de login
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

/**
 * Manipula o envio do formulário de login
 */
async function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById('email').value.trim();
    const senha = document.getElementById('senha').value;
    const loginBtn = document.getElementById('loginBtn');
    const errorMessage = document.getElementById('errorMessage');

    // Validação básica
    if (!email || !senha) {
        mostrarErroLogin('Por favor, preencha todos os campos.');
        return;
    }

    // Desabilita o botão e mostra loading
    loginBtn.disabled = true;
    loginBtn.innerHTML = '<span class="btn-text">AUTENTICANDO...</span>';
    ocultarErroLogin();

    try {
        // Tenta fazer login
        const response = await api.login(email, senha);

        if (response && response.usuario) {
            // Salva os dados do usuário
            salvarUsuarioLogado(response.usuario);

            // Registra o login no histórico (MongoDB)
            try {
                await api.registrarConsulta({
                    idUsuario: response.usuario.id,
                    nomeUsuario: response.usuario.nome,
                    emailUsuario: response.usuario.email,
                    tipoConsulta: 'LOGIN',
                    termoBusca: 'Login realizado',
                    resultadosIds: [],
                    ipOrigem: 'frontend',
                });
            } catch (err) {
                console.warn('Erro ao registrar login no histórico:', err);
                // Não bloqueia o login se falhar
            }

            // Mostra mensagem de sucesso
            mostrarSucessoLogin('Login realizado com sucesso! Redirecionando...');

            // Redireciona para o catálogo
            setTimeout(() => {
                window.location.href = 'catalogo.html';
            }, 1000);
        } else {
            mostrarErroLogin('Resposta inválida do servidor.');
            loginBtn.disabled = false;
            loginBtn.innerHTML = '<span class="btn-text">ACESSAR BIBLIOTECA</span><span class="btn-icon">⚡</span>';
        }
    } catch (error) {
        console.error('Erro no login:', error);
        
        let mensagemErro = 'Erro ao realizar login. Verifique suas credenciais.';
        
        if (error.message.includes('401') || error.message.includes('Credenciais')) {
            mensagemErro = 'Email ou senha incorretos.';
        } else if (error.message.includes('403') || error.message.includes('inativo')) {
            mensagemErro = 'Usuário inativo. Entre em contato com o administrador.';
        } else if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            mensagemErro = 'Erro de conexão. Verifique se o backend está rodando.';
        }

        mostrarErroLogin(mensagemErro);
        loginBtn.disabled = false;
        loginBtn.innerHTML = '<span class="btn-text">ACESSAR BIBLIOTECA</span><span class="btn-icon">⚡</span>';
    }
}

/**
 * Mostra mensagem de erro no formulário de login
 */
function mostrarErroLogin(mensagem) {
    const errorMessage = document.getElementById('errorMessage');
    if (errorMessage) {
        errorMessage.textContent = '⚠️ ' + mensagem;
        errorMessage.style.display = 'block';
    }
}

/**
 * Oculta mensagem de erro no formulário de login
 */
function ocultarErroLogin() {
    const errorMessage = document.getElementById('errorMessage');
    if (errorMessage) {
        errorMessage.style.display = 'none';
        errorMessage.textContent = '';
    }
}

/**
 * Mostra mensagem de sucesso no formulário de login
 */
function mostrarSucessoLogin(mensagem) {
    const errorMessage = document.getElementById('errorMessage');
    if (errorMessage) {
        errorMessage.textContent = '✓ ' + mensagem;
        errorMessage.className = 'success-message';
        errorMessage.style.display = 'block';
    }
}
