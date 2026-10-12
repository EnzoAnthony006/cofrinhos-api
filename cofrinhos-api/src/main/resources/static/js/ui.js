const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const percentual = new Intl.NumberFormat('pt-BR', { maximumFractionDigits: 1 });

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

export function humanizar(textoEnum) {
    const texto = String(textoEnum).replaceAll('_', ' ').toLowerCase();
    return texto.charAt(0).toUpperCase() + texto.slice(1);
}

export function mostrarToast(mensagem, tipo = 'info') {
    const area = document.getElementById('toasts');
    const toast = el('div', `toast toast--${tipo}`, mensagem);
    toast.setAttribute('role', 'status');
    area.append(toast);
    setTimeout(() => toast.remove(), 4000);
}