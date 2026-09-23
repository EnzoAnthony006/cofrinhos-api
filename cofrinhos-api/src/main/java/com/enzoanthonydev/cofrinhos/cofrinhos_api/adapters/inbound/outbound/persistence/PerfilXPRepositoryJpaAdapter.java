package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PerfilXPRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.Streak;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PerfilXPRepositoryJpaAdapter implements PerfilXPRepository {

    private final PerfilXPSpringDataRepository springDataRepository;

    public PerfilXPRepositoryJpaAdapter(PerfilXPSpringDataRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }
    @Override
    public PerfilXP salvar(PerfilXP perfilXP) {
        PerfilXPJpaEntity entitySalva = springDataRepository.save(paraEntity(perfilXP));
        return paraDominio(entitySalva);

    }
    @Override
    public Optional<PerfilXP> buscarPorUsuarioId(UUID usuarioId) {
        return springDataRepository.findById(usuarioId).map(this::paraDominio);
    }
    private PerfilXPJpaEntity paraEntity(PerfilXP perfilXP) {
        Streak streak = perfilXP.getStreakGlobal();
        return new PerfilXPJpaEntity(perfilXP.getUsuarioId(),
                perfilXP.getXpTotal(),
                streak.getSequenciaAtual(),
                streak.getMelhorSequencia(),
                streak.getDataUltimaAtividade()
        );
    }
    private PerfilXP paraDominio(PerfilXPJpaEntity entity) {
        Streak streak = Streak.reconstruir(entity.getSequenciaAtual(),
                entity.getMelhorSequencia(),
                entity.getDataUltimaAtividade()
        );
        return PerfilXP.reconstruir(entity.getUsuarioId(),
                entity.getXpTotal(),
                streak
        );
    }
}
