package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class BuscarPosicaoNoRankingUseCase {

    private final RankingXPRepository rankingXPRepository;

    public BuscarPosicaoNoRankingUseCase(RankingXPRepository rankingXPRepository) {
        this.rankingXPRepository = rankingXPRepository;
    }

    public Optional<PosicaoRanking> executar(UUID usuarioId) {
        return rankingXPRepository.buscarPosicao(usuarioId);
    }
}
