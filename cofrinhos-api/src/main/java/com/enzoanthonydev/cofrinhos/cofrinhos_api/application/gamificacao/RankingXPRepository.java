package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import java.util.List;
import java.util.UUID;

public interface RankingXPRepository {

    void atualizarXP(UUID usuarioId, int xpTotal);

    List<PosicaoRanking> buscarTop(int limite);
}
