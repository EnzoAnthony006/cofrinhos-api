import { iniciarAutenticacao, login, logout, usuarioAtual } from './auth.js';
import { listarCofrinhos } from './api.js';
import { el, formatarMoeda, formatarPercentual, humanizar } from './ui.js';

const telaLogin = document.getElementById('tela-login');
const avisoLogin = document.getElementById('aviso-login');
const barraTopo = document.getElementById('barra-topo');
const conteudo = document.getElementById('conteudo');
const nomeUsuario = document.getElementById('usuario-nome');
const listaCofrinhos = document.getElementById('lista-cofrinhos');
const abas = document.querySelectorAll('.tab');
const telas = document.querySelectorAll('.tela');

const ROTULOS_STATUS = { ATIVO: 'Ativo', CONCLUIDO: 'Concluído' };
const CLASSES_STATUS = { ATIVO: 'badge--ativo', CONCLUIDO: 'badge--concluido' };

function criarCartaoCofrinho(cofrinho) {
    const progresso = Math.max(0, Math.min(100, Number(cofrinho.progresso)));
    const cartao = el('article', 'card cofrinho');

    const cabecalho = el('div', 'cofrinho__cabecalho');
    cabecalho.append(
        el('h3', 'cofrinho__titulo', cofrinho.nome),
        el(
            'span',
            `badge ${CLASSES_STATUS[cofrinho.status] ?? ''}`,
            ROTULOS_STATUS[cofrinho.status] ?? humanizar(cofrinho.status),
        ),
    );
    cartao.append(cabecalho);

    if (cofrinho.descricao) {
        cartao.append(el('p', 'cofrinho__descricao', cofrinho.descricao));
    }

    const valores = el('div', 'cofrinho__valores');
    valores.append(
        el('strong', 'cofrinho__acumulado', formatarMoeda(cofrinho.valorAcumulado)),
        el('span', 'cofrinho__meta', `de ${formatarMoeda(cofrinho.valorMeta)}`),
    );
    cartao.append(valores);

    const barra = el('div', 'progress');
    barra.setAttribute('role', 'progressbar');
    barra.setAttribute('aria-label', `Progresso de ${cofrinho.nome}`);
    barra.setAttribute('aria-valuemin', '0');
    barra.setAttribute('aria-valuemax', '100');
    barra.setAttribute('aria-valuenow', String(Math.round(progresso)));
    const preenchimento = el('div', 'progress__bar');
    preenchimento.style.width = `${progresso}%`;
    barra.append(preenchimento);
    cartao.append(barra);

    const rodape = el('div', 'cofrinho__rodape');
    rodape.append(
        el('span', 'tag', humanizar(cofrinho.categoria)),
        el('span', null, formatarPercentual(progresso)),
    );
    cartao.append(rodape);

    return cartao;
}

function criarEstadoVazio() {
    const vazio = el('div', 'empty');
    vazio.append(
        el('h3', null, 'Nenhum cofrinho ainda'),
        el('p', null, 'Quando você criar seu primeiro cofrinho, ele aparece aqui.'),
    );
    return vazio;
}

async function carregarCofrinhos() {
    listaCofrinhos.replaceChildren(el('p', 'muted', 'Carregando seus cofrinhos...'));
    try {
        const cofrinhos = await listarCofrinhos();
        if (cofrinhos.length === 0) {
            listaCofrinhos.replaceChildren(criarEstadoVazio());
            return;
        }
        listaCofrinhos.replaceChildren(...cofrinhos.map(criarCartaoCofrinho));
    } catch (erro) {
        listaCofrinhos.replaceChildren(
            el('div', 'notice', `Não foi possível carregar seus cofrinhos: ${erro.message}`),
        );
    }
}

function mostrarTela(nome) {
    telas.forEach((tela) => {
        tela.hidden = tela.id !== `tela-${nome}`;
    });
    abas.forEach((aba) => {
        const ativa = aba.dataset.tela === nome;
        aba.classList.toggle('is-active', ativa);
        if (ativa) {
            aba.setAttribute('aria-current', 'page');
        } else {
            aba.removeAttribute('aria-current');
        }
    });
    if (nome === 'cofrinhos') {
        carregarCofrinhos();
    }
}

async function iniciar() {
    let autenticado = false;

    try {
        autenticado = await iniciarAutenticacao();
    } catch (erro) {
        avisoLogin.textContent = erro.message;
        avisoLogin.hidden = false;
    }

    telaLogin.hidden = autenticado;
    barraTopo.hidden = !autenticado;
    conteudo.hidden = !autenticado;

    if (autenticado) {
        nomeUsuario.textContent = usuarioAtual().nome;
        mostrarTela('cofrinhos');
    }
}

document.getElementById('btn-login').addEventListener('click', () => login());
document.getElementById('btn-logout').addEventListener('click', () => logout());
abas.forEach((aba) => {
    aba.addEventListener('click', () => mostrarTela(aba.dataset.tela));
});

iniciar();