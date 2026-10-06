package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReconstruirRankingXPUseCase {

    private final PerfilXPRepository perfilXPRepository;
    private final RankingXPRepository rankingXPRepository;

    public ReconstruirRankingXPUseCase(PerfilXPRepository perfilXPRepository, RankingXPRepository rankingXPRepository) {
        this.perfilXPRepository = perfilXPRepository;
        this.rankingXPRepository = rankingXPRepository;
    }
    public int executar () {
        List<PerfilXP> perfis = perfilXPRepository.buscarTodos();
        for (PerfilXP perfil : perfis) {
            rankingXPRepository.atualizarXP(perfil.getUsuarioId(), perfil.getXpTotal());
        }
        return perfis.size();
    }
}
