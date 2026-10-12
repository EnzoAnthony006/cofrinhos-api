import { iniciarAutenticacao, login, logout, usuarioAtual } from './auth.js';
import { buscarDashboard, listarCofrinhos, listarRanking } from './api.js';
import {
    abreviarId,
    el,
    formatarData,
    formatarMoeda,
    formatarNumero,
    formatarPercentual,
    humanizar,
    rotuloCategoria,
} from './ui.js';

const telaLogin = document.getElementById('tela-login');
const avisoLogin = document.getElementById('aviso-login');
const barraTopo = document.getElementById('barra-topo');
const conteudo = document.getElementById('conteudo');
const nomeUsuario = document.getElementById('usuario-nome');
const conteudoDashboard = document.getElementById('conteudo-dashboard');
const listaCofrinhos = document.getElementById('lista-cofrinhos');
const listaRanking = document.getElementById('lista-ranking');
const abas = document.querySelectorAll('.tab');
const telas = document.querySelectorAll('.tela');

const ROTULOS_STATUS = { ATIVO: 'Ativo', CONCLUIDO: 'Concluído' };
const CLASSES_STATUS = { ATIVO: 'badge--ativo', CONCLUIDO: 'badge--concluido' };
const LIMITE_RANKING = 10;


function criarEstatistica(rotulo, valor, dica) {
    const cartao = el('div', 'card stat');
    cartao.append(el('div', 'stat__label', rotulo), el('div', 'stat__value', valor));
    if (dica) {
        cartao.append(el('div', 'stat__hint', dica));
    }
    return cartao;
}

function criarSecaoConquistas(conquistas) {
    const secao = el('section');
    secao.append(el('h2', 'secao-titulo', 'Conquistas'));

    if (conquistas.length === 0) {
        const vazio = el('div', 'empty');
        vazio.append(
            el('h3', null, 'Nenhuma conquista ainda'),
            el('p', null, 'Registre aportes e mantenha sua sequência para desbloquear a primeira.'),
        );
        secao.append(vazio);
        return secao;
    }

    const plural = conquistas.length === 1 ? 'conquista desbloqueada' : 'conquistas desbloqueadas';
    secao.append(el('div', 'card', `${conquistas.length} ${plural}.`));
    return secao;
}

async function carregarDashboard() {
    conteudoDashboard.replaceChildren(el('p', 'muted', 'Carregando seu progresso...'));
    try {
        const { perfil, posicaoNoRanking, conquistas } = await buscarDashboard();

        const estatisticas = el('div', 'stats');
        estatisticas.append(
            criarEstatistica(
                'XP total',
                formatarNumero(perfil.xpTotal),
                `Última atividade: ${formatarData(perfil.dataUltimaAtividade)}`,
            ),
            criarEstatistica('Nível', formatarNumero(perfil.nivel)),
            criarEstatistica(
                'Sequência',
                formatarNumero(perfil.sequenciaAtual),
                `Melhor: ${formatarNumero(perfil.melhorSequencia)}`,
            ),
            criarEstatistica(
                'Posição no ranking',
                posicaoNoRanking ? `${posicaoNoRanking.posicao}º` : '—',
                posicaoNoRanking ? `${formatarNumero(posicaoNoRanking.xpTotal)} XP` : 'Faça um aporte para entrar',
            ),
        );

        conteudoDashboard.replaceChildren(estatisticas, criarSecaoConquistas(conquistas));
    } catch (erro) {
        conteudoDashboard.replaceChildren(
            el('div', 'notice', `Não foi possível carregar o dashboard: ${erro.message}`),
        );
    }
}


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
        el('span', 'tag', rotuloCategoria(cofrinho.categoria)),
        el('span', null, formatarPercentual(progresso)),
    );
    cartao.append(rodape);

    return cartao;
}

function criarEstadoVazioCofrinhos() {
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
            listaCofrinhos.replaceChildren(criarEstadoVazioCofrinhos());
            return;
        }
        listaCofrinhos.replaceChildren(...cofrinhos.map(criarCartaoCofrinho));
    } catch (erro) {
        listaCofrinhos.replaceChildren(
            el('div', 'notice', `Não foi possível carregar seus cofrinhos: ${erro.message}`),
        );
    }
}


function criarLinhaRanking(entrada, meuId) {
    const souEu = entrada.usuarioId === meuId;
    const linha = el('li', `card ranking__item${souEu ? ' ranking__item--eu' : ''}`);
    linha.append(
        el('span', 'ranking__pos', String(entrada.posicao)),
        el('span', 'ranking__nome', souEu ? 'Você' : `Jogador ${abreviarId(entrada.usuarioId)}`),
        el('span', 'ranking__xp', `${formatarNumero(entrada.xpTotal)} XP`),
    );
    return linha;
}

async function carregarRanking() {
    listaRanking.replaceChildren(el('p', 'muted', 'Carregando o ranking...'));
    try {
        const [ranking, dashboard] = await Promise.all([listarRanking(LIMITE_RANKING), buscarDashboard()]);
        const meuId = usuarioAtual().id;

        if (ranking.length === 0) {
            const vazio = el('div', 'empty');
            vazio.append(
                el('h3', null, 'Ranking vazio'),
                el('p', null, 'Ninguém pontuou ainda. Registre um aporte para abrir o ranking.'),
            );
            listaRanking.replaceChildren(vazio);
            return;
        }

        const lista = el('ol', 'ranking');
        ranking.forEach((entrada) => lista.append(criarLinhaRanking(entrada, meuId)));

        const estouNaLista = ranking.some((entrada) => entrada.usuarioId === meuId);
        if (!estouNaLista && dashboard.posicaoNoRanking) {
            lista.append(
                el('li', 'muted', 'Sua posição'),
                criarLinhaRanking(dashboard.posicaoNoRanking, meuId),
            );
        }

        listaRanking.replaceChildren(lista);
    } catch (erro) {
        listaRanking.replaceChildren(
            el('div', 'notice', `Não foi possível carregar o ranking: ${erro.message}`),
        );
    }
}


const CARREGADORES = {
    dashboard: carregarDashboard,
    cofrinhos: carregarCofrinhos,
    ranking: carregarRanking,
};

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
    CARREGADORES[nome]();
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
        mostrarTela('dashboard');
    }
}

document.getElementById('btn-login').addEventListener('click', () => login());
document.getElementById('btn-logout').addEventListener('click', () => logout());
document.getElementById('btn-atualizar-ranking').addEventListener('click', () => carregarRanking());
abas.forEach((aba) => {
    aba.addEventListener('click', () => mostrarTela(aba.dataset.tela));
});

iniciar();