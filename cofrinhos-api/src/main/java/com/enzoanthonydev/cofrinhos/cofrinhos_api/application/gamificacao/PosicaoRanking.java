package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import java.util.UUID;

public record PosicaoRanking(int posicao, UUID usuarioId, int xpTotal) {
}
