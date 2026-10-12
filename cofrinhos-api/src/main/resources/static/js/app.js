import { iniciarAutenticacao, login, logout, usuarioAtual } from './auth.js';
import {
    buscarDashboard,
    criarCofrinho,
    listarCofrinhos,
    listarRanking,
    registrarAporte,
} from './api.js';
import {
    ROTULOS_CATEGORIA,
    abreviarId,
    el,
    formatarData,
    formatarMoeda,
    formatarNumero,
    formatarPercentual,
    humanizar,
    mostrarToast,
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
const formNovoCofrinho = document.getElementById('form-novo-cofrinho');
const botaoNovoCofrinho = document.getElementById('btn-novo-cofrinho');
const botaoSalvarCofrinho = document.getElementById('btn-salvar-cofrinho');
const campoCategoria = document.getElementById('campo-categoria');
const abas = document.querySelectorAll('.tab');
const telas = document.querySelectorAll('.tela');

const ROTULOS_STATUS = { ATIVO: 'Ativo', CONCLUIDO: 'Concluído' };
const CLASSES_STATUS = { ATIVO: 'badge--ativo', CONCLUIDO: 'badge--concluido' };
const LIMITE_RANKING = 10;
const XP_POR_NIVEL = 500;
const TENTATIVAS_XP = 6;
const INTERVALO_XP_MS = 1000;

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


async function lerXpAtual() {
    try {
        const { perfil } = await buscarDashboard();
        return perfil.xpTotal;
    } catch {
        return null;
    }
}

async function anunciarXpGanho(xpAntes) {
    if (xpAntes === null) {
        return;
    }
    for (let tentativa = 0; tentativa < TENTATIVAS_XP; tentativa += 1) {
        await esperar(INTERVALO_XP_MS);
        const xpDepois = await lerXpAtual();
        if (xpDepois !== null && xpDepois > xpAntes) {
            mostrarToast(`+${formatarNumero(xpDepois - xpAntes)} XP`, 'xp');
            return;
        }
    }
}

async function aportar(cofrinho, valor, campo, botao) {
    botao.disabled = true;
    try {
        const xpAntes = await lerXpAtual();
        await registrarAporte(cofrinho.id, valor);
        mostrarToast(`Aporte de ${formatarMoeda(valor)} registrado em "${cofrinho.nome}".`);
        campo.value = '';
        await carregarCofrinhos();
        anunciarXpGanho(xpAntes);
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

prepararFormularioNovoCofrinho();
document.getElementById('btn-login').addEventListener('click', () => login());
document.getElementById('btn-logout').addEventListener('click', () => logout());
document.getElementById('btn-atualizar-ranking').addEventListener('click', () => carregarRanking());
abas.forEach((aba) => {
    aba.addEventListener('click', () => mostrarTela(aba.dataset.tela));
});

iniciar();