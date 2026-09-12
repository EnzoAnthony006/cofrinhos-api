package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao;

import java.time.LocalDate;
import java.util.UUID;

public class PerfilXP {

    private final UUID usuarioId;
    private int xpTotal;
    private final Streak streakGlobal;

    private PerfilXP(UUID usuarioId, int xpTotal, Streak streakGlobal) {
        this.usuarioId = usuarioId;
        this.xpTotal = xpTotal;
        this.streakGlobal = streakGlobal;
    }
    public static PerfilXP iniciar (UUID usuarioId) {
        return new PerfilXP(usuarioId,0, Streak.iniciar());
    }
    public void adicionarXP (int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade de XP não pode ser negativa");
        }
        this.xpTotal += quantidade;
    }
    public void registrarAtividadeStreak(LocalDate data) {
        streakGlobal.registrarAtividade(data);
    }
    public int getNivel() {
        return (xpTotal / 500 ) + 1;
    }
    public UUID getUsuarioId() {
        return usuarioId;
    }
    public int getXpTotal() {
        return xpTotal;
    }
    public Streak getStreakGlobal() {
        return streakGlobal;
    }
}
