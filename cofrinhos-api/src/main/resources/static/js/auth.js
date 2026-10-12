const KEYCLOAK_URL = 'http://localhost:8081';
const REALM = 'cofrinhos';
const CLIENT_ID = 'cofrinhos-web';
const REDIRECT_URI = `${window.location.origin}/`;

const OIDC = `${KEYCLOAK_URL}/realms/${REALM}/protocol/openid-connect`;
const CHAVE_TOKENS = 'cofrinhos.tokens';
const CHAVE_PKCE = 'cofrinhos.pkce';
const MARGEM_RENOVACAO_MS = 30_000;

let renovacaoEmAndamento = null;

function lerStorage(chave) {
    try {
        const bruto = sessionStorage.getItem(chave);
        return bruto ? JSON.parse(bruto) : null;
    } catch {
        return null;
    }
}

function gravarStorage(chave, valor) {
    try {
        sessionStorage.setItem(chave, JSON.stringify(valor));
    } catch {

    }
}

function removerStorage(chave) {
    try {
        sessionStorage.removeItem(chave);
    } catch {

    }
}

function base64Url(bytes) {
    let binario = '';
    bytes.forEach((b) => {
        binario += String.fromCharCode(b);
    });
    return btoa(binario).replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
}

function textoAleatorio(tamanhoEmBytes) {
    const bytes = new Uint8Array(tamanhoEmBytes);
    crypto.getRandomValues(bytes);
    return base64Url(bytes);
}

async function desafioPkce(verificador) {
    const digest = await crypto.subtle.digest('SHA-256', new TextEncoder().encode(verificador));
    return base64Url(new Uint8Array(digest));
}

function decodificarJwt(token) {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const binario = atob(payload.padEnd(Math.ceil(payload.length / 4) * 4, '='));
    const bytes = Uint8Array.from(binario, (c) => c.charCodeAt(0));
    return JSON.parse(new TextDecoder().decode(bytes));
}

function limparUrl() {
    window.history.replaceState({}, document.title, window.location.pathname);
}

async function pedirToken(corpo) {
    const resposta = await fetch(`${OIDC}/token`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams(corpo),
    });
    if (!resposta.ok) {
        throw new Error(`Falha ao obter token (${resposta.status})`);
    }
    const dados = await resposta.json();
    gravarStorage(CHAVE_TOKENS, {
        accessToken: dados.access_token,
        refreshToken: dados.refresh_token,
        idToken: dados.id_token,
        expiraEm: Date.now() + dados.expires_in * 1000,
    });
}

export async function login() {
    const verificador = textoAleatorio(64);
    const state = textoAleatorio(16);
    gravarStorage(CHAVE_PKCE, { verificador, state });

    const parametros = new URLSearchParams({
        response_type: 'code',
        client_id: CLIENT_ID,
        redirect_uri: REDIRECT_URI,
        scope: 'openid',
        state,
        code_challenge: await desafioPkce(verificador),
        code_challenge_method: 'S256',
    });
    window.location.assign(`${OIDC}/auth?${parametros}`);
}

export async function iniciarAutenticacao() {
    const url = new URL(window.location.href);
    const erro = url.searchParams.get('error');
    const code = url.searchParams.get('code');
    const state = url.searchParams.get('state');

    if (erro) {
        const detalhe = url.searchParams.get('error_description') ?? erro;
        limparUrl();
        throw new Error(`Login recusado: ${detalhe}`);
    }

    if (code) {
        const pkce = lerStorage(CHAVE_PKCE);
        removerStorage(CHAVE_PKCE);
        limparUrl();
        if (!pkce || pkce.state !== state) {
            throw new Error('Estado de login inválido. Tente entrar novamente.');
        }
        await pedirToken({
            grant_type: 'authorization_code',
            code,
            redirect_uri: REDIRECT_URI,
            client_id: CLIENT_ID,
            code_verifier: pkce.verificador,
        });
    }

    return lerStorage(CHAVE_TOKENS) !== null;
}

async function renovar(tokens) {
    if (!renovacaoEmAndamento) {
        renovacaoEmAndamento = pedirToken({
            grant_type: 'refresh_token',
            refresh_token: tokens.refreshToken,
            client_id: CLIENT_ID,
        })
            .catch(async (erro) => {
                removerStorage(CHAVE_TOKENS);
                await login();
                throw erro;
            })
            .finally(() => {
                renovacaoEmAndamento = null;
            });
    }
    await renovacaoEmAndamento;
    return lerStorage(CHAVE_TOKENS).accessToken;
}

export async function obterAccessToken() {
    const tokens = lerStorage(CHAVE_TOKENS);
    if (!tokens) {
        await login();
        throw new Error('Sessão ausente: redirecionando para o login');
    }
    if (tokens.expiraEm - Date.now() > MARGEM_RENOVACAO_MS) {
        return tokens.accessToken;
    }
    return renovar(tokens);
}

export function usuarioAtual() {
    const tokens = lerStorage(CHAVE_TOKENS);
    if (!tokens) {
        return null;
    }
    const claims = decodificarJwt(tokens.accessToken);
    return {
        id: claims.sub,
        nome: claims.preferred_username ?? claims.sub,
        roles: claims.realm_access?.roles ?? [],
    };
}

export function logout() {
    const tokens = lerStorage(CHAVE_TOKENS);
    removerStorage(CHAVE_TOKENS);
    const parametros = new URLSearchParams({
        client_id: CLIENT_ID,
        post_logout_redirect_uri: REDIRECT_URI,
    });
    if (tokens?.idToken) {
        parametros.set('id_token_hint', tokens.idToken);
    }
    window.location.assign(`${OIDC}/logout?${parametros}`);
}