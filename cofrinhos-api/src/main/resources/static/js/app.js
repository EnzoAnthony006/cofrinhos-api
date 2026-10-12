import { iniciarAutenticacao, login, logout, usuarioAtual } from './auth.js';
import { listarCofrinhos } from './api.js';

const status = document.getElementById('status');
const botaoLogin = document.getElementById('btn-login');
const botaoLogout = document.getElementById('btn-logout');
const botaoListar = document.getElementById('btn-listar');
const saida = document.getElementById('saida');

async function iniciar() {
    let mensagem = 'Você não está logado.';
    let autenticado = false;

    try {
        autenticado = await iniciarAutenticacao();
    } catch (erro) {
        mensagem = erro.message;
    }

    if (autenticado) {
        mensagem = `Logado como ${usuarioAtual().nome}`;
    }

    status.textContent = mensagem;
    botaoLogin.hidden = autenticado;
    botaoLogout.hidden = !autenticado;
    botaoListar.hidden = !autenticado;
}

botaoLogin.addEventListener('click', () => login());
botaoLogout.addEventListener('click', () => logout());
botaoListar.addEventListener('click', async () => {
    try {
        const cofrinhos = await listarCofrinhos();
        saida.textContent = JSON.stringify(cofrinhos, null, 2);
    } catch (erro) {
        saida.textContent = `Erro: ${erro.message}`;
    }
});

iniciar();