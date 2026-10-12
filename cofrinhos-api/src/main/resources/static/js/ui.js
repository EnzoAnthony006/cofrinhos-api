const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const percentual = new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 1 });
const inteiro = new Intl.NumberFormat('pt-BR');

export const ROTULOS_CATEGORIA = {
    RENDA_FIXA: 'Renda fixa',
    RENDA_VARIAVEL: 'Renda variável',
    RESERVA_EMERGENCIA: 'Reserva de emergência',
    ECONOMIA: 'Economia',
};

export const ROTULOS_CONQUISTA = {
    PRIMEIRO_COFRINHO_CONCLUIDO: 'Primeiro cofrinho concluído',
    PRIMEIRA_RESERVA_EMERGENCIA_COMPLETA: 'Reserva de emergência completa',
    STREAK_QUATRO_SEMANAS: 'Streak de 4 semanas',
};

export function el(tag, classe, texto) {
    const elemento = document.createElement(tag);
    if (classe) {
        elemento.className = classe;
    }
    if (texto !== undefined) {
        elemento.textContent = texto;
    }
    return elemento;
}

export function formatarMoeda(valor) {
    return moeda.format(Number(valor));
}

export function formatarPercentual(valor) {
    return `${percentual.format(Number(valor))}%`;
}

export function formatarNumero(valor) {
    return inteiro.format(Number(valor));
}

export function formatarData(dataIso) {
    if (!dataIso) {
        return '—';
    }
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
}

export function formatarDataHora(dataHoraIso) {
    if (!dataHoraIso) {
        return '—';
    }
    const [data, hora = ''] = dataHoraIso.split('T');
    return `${formatarData(data)} ${hora.slice(0, 5)}`.trim();
}

export function abreviarId(uuid) {
    return String(uuid).slice(0, 8);
}

export function humanizar(textoEnum) {
    const texto = String(textoEnum).replaceAll('_', ' ').toLowerCase();
    return texto.charAt(0).toUpperCase() + texto.slice(1);
}

export function rotuloCategoria(categoria) {
    return ROTULOS_CATEGORIA[categoria] ?? humanizar(categoria);
}

export function rotuloConquista(definicao) {
    return ROTULOS_CONQUISTA[definicao] ?? humanizar(definicao);
}

export function mostrarToast(mensagem, tipo = 'info') {
    const area = document.getElementById('toasts');
    const toast = el('div', `toast toast--${tipo}`, mensagem);
    toast.setAttribute('role', 'status');
    area.append(toast);
    setTimeout(() => toast.remove(), 4000);
}