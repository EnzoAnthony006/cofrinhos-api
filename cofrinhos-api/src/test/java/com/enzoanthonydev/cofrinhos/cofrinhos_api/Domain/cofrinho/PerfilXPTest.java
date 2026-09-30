package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.Streak;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PerfilXPTest {

    private final UUID usuarioId = UUID.randomUUID();

    @Test
    void deveIniciarComXpZeroENivelUm() {
        PerfilXP perfilXP = PerfilXP.iniciar(usuarioId);

        assertEquals(usuarioId, perfilXP.getUsuarioId());
        assertEquals(0, perfilXP.getXpTotal());
        assertEquals(1, perfilXP.getNivel());
        assertEquals(0, perfilXP.getStreakGlobal().getSequenciaAtual());
    }

    @Test
    void deveAcumularXPAoAdicionarVariasVezes() {
        PerfilXP perfilXP = PerfilXP.iniciar(usuarioId);

        perfilXP.adicionarXP(30);
        perfilXP.adicionarXP(20);

        assertEquals(50, perfilXP.getXpTotal());
    }

    @Test
    void naoDevePermitirAdicionarXPNegativo() {
        PerfilXP perfilXP = PerfilXP.iniciar(usuarioId);

        assertThrows(IllegalArgumentException.class, () -> perfilXP.adicionarXP(-10));
    }

    @Test
    void deveSubirDeNivelAoAtingirQuinhentosXP() {
        PerfilXP perfilXP = PerfilXP.iniciar(usuarioId);

        perfilXP.adicionarXP(499);
        assertEquals(1, perfilXP.getNivel());

        perfilXP.adicionarXP(1);
        assertEquals(2, perfilXP.getNivel());
    }

    @Test
    void deveDelegarRegistroDeAtividadeParaOStreak() {
        PerfilXP perfilXP = PerfilXP.iniciar(usuarioId);
        LocalDate hoje = LocalDate.of(2026, 1, 1);

        perfilXP.registrarAtividadeStreak(hoje);

        assertEquals(1, perfilXP.getStreakGlobal().getSequenciaAtual());
        assertEquals(hoje, perfilXP.getStreakGlobal().getDataUltimaAtividade());
    }

    @Test
    void reconstruirDeveManterEstadoExato() {
        Streak streak = Streak.reconstruir(3, 5, LocalDate.of(2026, 2, 1));

        PerfilXP perfilXP = PerfilXP.reconstruir(usuarioId, 750, streak);

        assertEquals(usuarioId, perfilXP.getUsuarioId());
        assertEquals(750, perfilXP.getXpTotal());
        assertEquals(2, perfilXP.getNivel());
        assertEquals(3, perfilXP.getStreakGlobal().getSequenciaAtual());
    }
}
