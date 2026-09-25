package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;

import java.time.LocalDate;
import java.util.UUID;

public record PerfilXPResponse(
        UUID usuarioId,
        int xpTotal,
        int nivel,
        int sequenciaAtual,
        int melhorSequencia,
        LocalDate dataUltimaAtividade
) {
    public static PerfilXPResponse de(PerfilXP perfilXP) {
        return new PerfilXPResponse(
                perfilXP.getUsuarioId(),
                perfilXP.getXpTotal(),
                perfilXP.getNivel(),
                perfilXP.getStreakGlobal().getSequenciaAtual(),
                perfilXP.getStreakGlobal().getMelhorSequencia(),
                perfilXP.getStreakGlobal().getDataUltimaAtividade()
        );
    }
}
