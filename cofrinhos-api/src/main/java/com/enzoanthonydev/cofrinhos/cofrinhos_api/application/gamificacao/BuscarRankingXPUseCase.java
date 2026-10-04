package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuscarRankingXPUseCase {

    private static final int LIMITE_MAXIMO = 100;

    private final RankingXPRepository rankingXPRepository;

    public BuscarRankingXPUseCase(RankingXPRepository rankingXPRepository) {
        this.rankingXPRepository = rankingXPRepository;
    }
    public List<PosicaoRanking> executar(int limite) {
        if ( limite < 1 || limite > LIMITE_MAXIMO ) {
            throw new IllegalArgumentException("O limite deve estar entre 1 e " + LIMITE_MAXIMO);
        }
        return rankingXPRepository.buscarTop(limite);
    }
}
