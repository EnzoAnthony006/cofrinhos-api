import { iniciarAutenticacao, login, logout, usuarioAtual } from './auth.js';
import {
    acompanharRanking,
    buscarDashboard,
    criarCofrinho,
    listarCofrinhos,
    registrarAporte,
} from './api.js';
import {
    ROTULOS_CATEGORIA,
    abreviarId,
    el,
    formatarData,
    formatarDataHora,
    formatarMoeda,
    formatarNumero,
    formatarPercentual,
    humanizar,
    mostrarToast,
    rotuloCategoria,
    rotuloConquista,
} from './ui.js';

const telaLogin = document.getElementById('tela-login');
const avisoLogin = document.getElementById('aviso-login');
const barraTopo = document.getElementById('barra-topo');
const conteudo = document.getElementById('conteudo');
const nomeUsuario = document.getElementById('usuario-nome');
const conteudoDashboard = document.getElementById('conteudo-dashboard');
const listaCofrinhos = document.getElementById('lista-cofrinhos');
const listaRanking = document.getElementById('lista-ranking');
const estadoRanking = document.getElementById('estado-ranking');
const estadoRankingTexto = document.getElementById('estado-ranking-texto');
const formNovoCofrinho = document.getElementById('form-novo-cofrinho');
const botaoNovoCofrinho = document.getElementById('btn-novo-cofrinho');
const botaoSalvarCofrinho = document.getElementById('btn-salvar-cofrinho');
const campoCategoria = document.getElementById('campo-categoria');
const abas = document.querySelectorAll('.tab');
const telas = document.querySelectorAll('.tela');

const ROTULOS_STATUS = { ATIVO: 'Ativo', CONCLUIDO: 'Concluído' };
const CLASSES_STATUS = { ATIVO: 'badge--ativo', CONCLUIDO: 'badge--concluido' };
const TEXTOS_ESTADO_RANKING = {
    conectando: 'Conectando...',
    aovivo: 'Ao vivo',
    reconectando: 'Reconectando...',
};
const LIMITE_RANKING = 10;
const XP_POR_NIVEL = 500;
const TENTATIVAS_XP = 6;
const INTERVALO_XP_MS = 1000;
const RECONEXAO_RANKING_MS = 2000;

function esperar(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
}

function criarBarraProgresso(percentual, rotulo) {
    const barra = el('div', 'progress');
    barra.setAttribute('role', 'progressbar');
    barra.setAttribute('aria-label', rotulo);
    barra.setAttribute('aria-valuemin', '0');
    barra.setAttribute('aria-valuemax', '100');
    barra.setAttribute('aria-valuenow', String(Math.round(percentual)));
    const preenchimento = el('div', 'progress__bar');
    preenchimento.style.width = `${percentual}%`;
    barra.append(preenchimento);
    return barra;
}


function criarEstatistica(rotulo, valor, dica) {
    const cartao = el('div', 'card stat');
    cartao.append(el('div', 'stat__label', rotulo), el('div', 'stat__value', valor));
    if (dica) {
        cartao.append(el('div', 'stat__hint', dica));
    }
    return cartao;
}

function criarCartaoNivel(perfil) {
    const xpNoNivel = perfil.xpTotal % XP_POR_NIVEL;
    const restante = XP_POR_NIVEL - xpNoNivel;
    const cartao = criarEstatistica('Nível', formatarNumero(perfil.nivel));
    cartao.append(
        criarBarraProgresso((xpNoNivel / XP_POR_NIVEL) * 100, 'Progresso até o próximo nível'),
        el('div', 'stat__hint', `${formatarNumero(restante)} XP para o nível ${perfil.nivel + 1}`),
    );
    return cartao;
}

function criarCartaoConquista(conquista) {
    const cartao = el('article', 'card conquista');
    const icone = el('span', 'conquista__icone', '★');
    icone.setAttribute('aria-hidden', 'true');

    const texto = el('div');
    texto.append(
        el('strong', null, rotuloConquista(conquista.definicao)),
        el('div', 'stat__hint', `Desbloqueada em ${formatarDataHora(conquista.dataDesbloqueio)}`),
    );

    cartao.append(icone, texto);
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

    const grade = el('div', 'grid');
    grade.append(...conquistas.map(criarCartaoConquista));
    secao.append(grade);
    return secao;
}

async function carregarDashboard() {
    conteudoDashboard.replaceChildren(el('p', 'muted', 'Carregando seu progresso...'));
    try {
        const { perfil, posicaoNoRanking, conquistas } = await buscarDashboard();
        const semanas = perfil.sequenciaAtual === 1 ? 'semana' : 'semanas';

        const estatisticas = el('div', 'stats');
        estatisticas.append(
            criarEstatistica(
                'XP total',
                formatarNumero(perfil.xpTotal),
                `Última atividade: ${formatarData(perfil.dataUltimaAtividade)}`,
            ),
            criarCartaoNivel(perfil),
            criarEstatistica(
                'Sequência',
                `${formatarNumero(perfil.sequenciaAtual)} ${semanas}`,
                `Melhor sequência: ${formatarNumero(perfil.melhorSequencia)}`,
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


async function lerGamificacao() {
    try {
        const { perfil, conquistas } = await buscarDashboard();
        return {
            xp: perfil.xpTotal,
            conquistas: new Set(conquistas.map((conquista) => conquista.definicao)),
        };
    } catch {
        return null;
    }
}

async function anunciarGanhos(antes) {
    if (antes === null) {
        return;
    }
    for (let tentativa = 0; tentativa < TENTATIVAS_XP; tentativa += 1) {
        await esperar(INTERVALO_XP_MS);
        const depois = await lerGamificacao();
        if (depois !== null && depois.xp > antes.xp) {
            mostrarToast(`+${formatarNumero(depois.xp - antes.xp)} XP`, 'xp');

            await esperar(INTERVALO_XP_MS);
            const final = (await lerGamificacao()) ?? depois;
            final.conquistas.forEach((definicao) => {
                if (!antes.conquistas.has(definicao)) {
                    mostrarToast(`Conquista desbloqueada: ${rotuloConquista(definicao)}`, 'xp');
                }
            });
            return;
        }
    }
}

async function aportar(cofrinho, valor, campo, botao) {
    botao.disabled = true;
    try {
        const antes = await lerGamificacao();
        await registrarAporte(cofrinho.id, valor);
        mostrarToast(`Aporte de ${formatarMoeda(valor)} registrado em "${cofrinho.nome}".`);
        campo.value = '';
        await carregarCofrinhos();
        anunciarGanhos(antes);
    } catch (erro) {
        mostrarToast(erro.message, 'erro');
    } finally {
        botao.disabled = false;
    }
}

function criarFormularioAporte(cofrinho) {
    const form = el('form', 'aporte');
    const campo = el('input', 'input');
    campo.type = 'number';
    campo.min = '0.01';
    campo.step = '0.01';
    campo.required = true;
    campo.inputMode = 'decimal';
    campo.placeholder = 'Valor do aporte (R$)';
    campo.setAttribute('aria-label', `Valor do aporte em ${cofrinho.nome}`);

    const botao = el('button', 'btn btn--primary', 'Aportar');
    botao.type = 'submit';

    form.append(campo, botao);
    form.addEventListener('submit', (evento) => {
        evento.preventDefault();
        aportar(cofrinho, Number(campo.value), campo, botao);
    });
    return form;
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

    cartao.append(criarBarraProgresso(progresso, `Progresso de ${cofrinho.nome}`));

    const rodape = el('div', 'cofrinho__rodape');
    rodape.append(
        el('span', 'tag', rotuloCategoria(cofrinho.categoria)),
        el('span', null, formatarPercentual(progresso)),
    );
    cartao.append(rodape);

    if (cofrinho.status === 'ATIVO') {
        cartao.append(criarFormularioAporte(cofrinho));
    }

    return cartao;
}

function criarEstadoVazioCofrinhos() {
    const vazio = el('div', 'empty');
    vazio.append(
        el('h3', null, 'Nenhum cofrinho ainda'),
        el('p', null, 'Clique em "Novo cofrinho" para criar o primeiro.'),
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

function abrirFormularioNovoCofrinho() {
    formNovoCofrinho.hidden = false;
    document.getElementById('campo-nome').focus();
}

function fecharFormularioNovoCofrinho() {
    formNovoCofrinho.reset();
    formNovoCofrinho.hidden = true;
}

function prepararFormularioNovoCofrinho() {
    Object.entries(ROTULOS_CATEGORIA).forEach(([valor, rotulo]) => {
        const opcao = el('option', null, rotulo);
        opcao.value = valor;
        campoCategoria.append(opcao);
    });

    botaoNovoCofrinho.addEventListener('click', abrirFormularioNovoCofrinho);
    document.getElementById('btn-cancelar-cofrinho').addEventListener('click', fecharFormularioNovoCofrinho);

    formNovoCofrinho.addEventListener('submit', async (evento) => {
        evento.preventDefault();
        const dados = new FormData(formNovoCofrinho);
        botaoSalvarCofrinho.disabled = true;
        try {
            await criarCofrinho({
                nome: String(dados.get('nome')).trim(),
                descricao: String(dados.get('descricao')).trim(),
                categoria: dados.get('categoria'),
                valorMeta: Number(dados.get('valorMeta')),
            });
            mostrarToast('Cofrinho criado!');
            fecharFormularioNovoCofrinho();
            await carregarCofrinhos();
        } catch (erro) {
            mostrarToast(erro.message, 'erro');
        } finally {
            botaoSalvarCofrinho.disabled = false;
        }
    });
}


let controladorRanking = null;
let ultimaChaveRanking = null;
let xpAnteriorRanking = null;
let minhaPosicao = null;

function definirEstadoRanking(estado) {
    estadoRanking.dataset.estado = estado;
    estadoRankingTexto.textContent = TEXTOS_ESTADO_RANKING[estado];
}

function criarLinhaRanking(entrada, meuId, mudou) {
    const souEu = entrada.usuarioId === meuId;
    const classes = ['card', 'ranking__item'];
    if (souEu) {
        classes.push('ranking__item--eu');
    }
    if (mudou) {
        classes.push('ranking__item--atualizado');
    }

    const linha = el('li', classes.join(' '));
    linha.append(
        el('span', 'ranking__pos', String(entrada.posicao)),
        el('span', 'ranking__nome', souEu ? 'Você' : `Jogador ${abreviarId(entrada.usuarioId)}`),
        el('span', 'ranking__xp', `${formatarNumero(entrada.xpTotal)} XP`),
    );
    return linha;
}

function renderizarRanking(entradas) {
    const meuId = usuarioAtual().id;

    if (entradas.length === 0) {
        const vazio = el('div', 'empty');
        vazio.append(
            el('h3', null, 'Ranking vazio'),
            el('p', null, 'Ninguém pontuou ainda. Registre um aporte para abrir o ranking.'),
        );
        listaRanking.replaceChildren(vazio);
        return;
    }

    const lista = el('ol', 'ranking');
    entradas.forEach((entrada) => {
        const mudou = xpAnteriorRanking !== null && xpAnteriorRanking.get(entrada.usuarioId) !== entrada.xpTotal;
        lista.append(criarLinhaRanking(entrada, meuId, mudou));
    });

    const estouNaLista = entradas.some((entrada) => entrada.usuarioId === meuId);
    if (!estouNaLista && minhaPosicao) {
        lista.append(el('li', 'muted', 'Sua posição'), criarLinhaRanking(minhaPosicao, meuId, false));
    }

    listaRanking.replaceChildren(lista);
    xpAnteriorRanking = new Map(entradas.map((entrada) => [entrada.usuarioId, entrada.xpTotal]));
}

async function atualizarMinhaPosicao(entradas, chave) {
    try {
        const { posicaoNoRanking } = await buscarDashboard();
        minhaPosicao = posicaoNoRanking;
        if (ultimaChaveRanking === chave) {
            renderizarRanking(entradas);
        }
    } catch {
    }
}

function aoReceberRanking(entradas) {
    definirEstadoRanking('aovivo');

    const chave = JSON.stringify(entradas);
    if (chave === ultimaChaveRanking) {
        return;
    }
    ultimaChaveRanking = chave;
    renderizarRanking(entradas);

    const meuId = usuarioAtual().id;
    if (!entradas.some((entrada) => entrada.usuarioId === meuId)) {
        atualizarMinhaPosicao(entradas, chave);
    }
}

function pararRankingAoVivo() {
    if (controladorRanking) {
        controladorRanking.abort();
        controladorRanking = null;
    }
}

async function iniciarRankingAoVivo() {
    pararRankingAoVivo();
    const controlador = new AbortController();
    controladorRanking = controlador;
    ultimaChaveRanking = null;
    xpAnteriorRanking = null;
    minhaPosicao = null;

    listaRanking.replaceChildren(el('p', 'muted', 'Conectando ao ranking ao vivo...'));
    definirEstadoRanking('conectando');

    let primeiraConexao = true;
    while (!controlador.signal.aborted) {
        try {
            if (!primeiraConexao) {
                definirEstadoRanking('reconectando');
            }
            primeiraConexao = false;
            await acompanharRanking(LIMITE_RANKING, aoReceberRanking, controlador.signal);
        } catch (erro) {
            if (controlador.signal.aborted) {
                return;
            }
            definirEstadoRanking('reconectando');
        }
        if (controlador.signal.aborted) {
            return;
        }
        await esperar(RECONEXAO_RANKING_MS);
    }
}


const CARREGADORES = {
    dashboard: carregarDashboard,
    cofrinhos: carregarCofrinhos,
    ranking: iniciarRankingAoVivo,
};

function mostrarTela(nome) {
    if (nome !== 'ranking') {
        pararRankingAoVivo();
    }
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

prepararFormularioNovoCofrinho();
document.getElementById('btn-login').addEventListener('click', () => login());
document.getElementById('btn-logout').addEventListener('click', () => logout());
abas.forEach((aba) => {
    aba.addEventListener('click', () => mostrarTela(aba.dataset.tela));
});
window.addEventListener('pagehide', pararRankingAoVivo);

iniciar();