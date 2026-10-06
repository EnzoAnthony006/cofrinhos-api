package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class BuscarPosicaoNoRankingUseCase {

    private final RankingXPRepository rankingXPRepository;

    public BuscarPosicaoNoRankingUseCase(RankingXPRepository rankingXPRepository) {
        this.rankingXPRepository = rankingXPRepository;
    }
    public Optional<PosicaoRanking> buscarPosicao(UUID usuarioId) {
        return rankingXPRepository.buscarPosicao(usuarioId);
    }
}
