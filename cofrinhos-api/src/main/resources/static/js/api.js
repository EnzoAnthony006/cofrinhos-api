import { login, obterAccessToken } from './auth.js';

export class ApiError extends Error {
    constructor(status, mensagem) {
        super(mensagem);
        this.status = status;
    }
}

async function requisitar(caminho, opcoes = {}) {
    const token = await obterAccessToken();
    const headers = { ...opcoes.headers, Authorization: `Bearer ${token}` };
    if (opcoes.body) {
        headers['Content-Type'] = 'application/json';
    }

    const resposta = await fetch(caminho, { ...opcoes, headers });

    if (resposta.status === 401) {
        await login();
        throw new ApiError(401, 'Sessão expirada: redirecionando para o login');
    }
    if (!resposta.ok) {
        let detalhe = `Erro ${resposta.status}`;
        try {
            const problema = await resposta.json();
            detalhe = problema.detail ?? problema.title ?? detalhe;
        } catch {
        }
        throw new ApiError(resposta.status, detalhe);
    }
    return resposta.status === 204 ? null : resposta.json();
}

export function listarCofrinhos() {
    return requisitar('/cofrinhos');
}

export function buscarCofrinho(id) {
    return requisitar(`/cofrinhos/${id}`);
}

export function criarCofrinho({ nome, descricao, categoria, valorMeta }) {
    return requisitar('/cofrinhos', {
        method: 'POST',
        body: JSON.stringify({ nome, descricao, categoria, valorMeta }),
    });
}

export function registrarAporte(cofrinhoId, valor) {
    return requisitar(`/cofrinhos/${cofrinhoId}/aportes`, {
        method: 'POST',
        body: JSON.stringify({ valor }),
    });
}

export function buscarDashboard() {
    return requisitar('/dashboard');
}

export function listarRanking(limite = 10) {
    return requisitar(`/ranking?limite=${limite}`);
}


export async function acompanharRanking(limite, aoReceber, sinal) {
    const token = await obterAccessToken();
    const resposta = await fetch(`/ranking/stream?limite=${limite}`, {
        headers: { Authorization: `Bearer ${token}`, Accept: 'text/event-stream' },
        signal: sinal,
    });

    if (resposta.status === 401) {
        await login();
        throw new ApiError(401, 'Sessão expirada: redirecionando para o login');
    }
    if (!resposta.ok || !resposta.body) {
        throw new ApiError(resposta.status, `Erro ${resposta.status} ao abrir o ranking ao vivo`);
    }

    const leitor = resposta.body.getReader();
    const decodificador = new TextDecoder();
    let pendente = '';

    for (;;) {
        const { done, value } = await leitor.read();
        if (done) {
            return;
        }
        pendente += decodificador.decode(value, { stream: true });

        const eventos = pendente.split(/\r?\n\r?\n/);
        pendente = eventos.pop();

        for (const evento of eventos) {
            const dados = evento
                .split(/\r?\n/)
                .filter((linha) => linha.startsWith('data:'))
                .map((linha) => linha.slice(5).trimStart())
                .join('\n');
            if (dados) {
                aoReceber(JSON.parse(dados));
            }
        }
    }
}