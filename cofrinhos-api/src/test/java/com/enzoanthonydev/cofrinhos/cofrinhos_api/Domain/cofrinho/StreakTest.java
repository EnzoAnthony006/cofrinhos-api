package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.Streak;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StreakTest {

    @Test
    void deveIniciarZerado() {
        Streak streak = Streak.iniciar();

        assertEquals(0, streak.getSequenciaAtual());
        assertEquals(0, streak.getMelhorSequencia());
        assertNull(streak.getDataUltimaAtividade());
    }

    @Test
    void devePrimeiraAtividadeIniciarSequenciaEmUm() {
        Streak streak = Streak.iniciar();
        LocalDate hoje = LocalDate.of(2026, 1, 1);

        streak.registrarAtividade(hoje);

        assertEquals(1, streak.getSequenciaAtual());
        assertEquals(1, streak.getMelhorSequencia());
        assertEquals(hoje, streak.getDataUltimaAtividade());
    }

    @Test
    void deveIncrementarSequenciaQuandoAtividadeDentroDeSeteDias() {
        Streak streak = Streak.iniciar();
        LocalDate semana1 = LocalDate.of(2026, 1, 1);
        LocalDate semana2 = semana1.plusDays(7);

        streak.registrarAtividade(semana1);
        streak.registrarAtividade(semana2);

        assertEquals(2, streak.getSequenciaAtual());
        assertEquals(2, streak.getMelhorSequencia());
    }

    @Test
    void deveReiniciarSequenciaQuandoIntervaloMaiorQueSeteDias() {
        Streak streak = Streak.iniciar();
        LocalDate semana1 = LocalDate.of(2026, 1, 1);
        LocalDate semana2 = semana1.plusDays(7);
        LocalDate depoisDoIntervalo = semana2.plusDays(8);

        streak.registrarAtividade(semana1);
        streak.registrarAtividade(semana2);
        streak.registrarAtividade(depoisDoIntervalo);

        assertEquals(1, streak.getSequenciaAtual());
    }

    @Test
    void deveManterMelhorSequenciaMesmoDepoisDeReiniciar() {
        Streak streak = Streak.iniciar();
        LocalDate semana1 = LocalDate.of(2026, 1, 1);
        LocalDate semana2 = semana1.plusDays(7);
        LocalDate semana3 = semana2.plusDays(7);
        LocalDate depoisDoIntervalo = semana3.plusDays(8);

        streak.registrarAtividade(semana1);
        streak.registrarAtividade(semana2);
        streak.registrarAtividade(semana3);
        streak.registrarAtividade(depoisDoIntervalo);

        assertEquals(1, streak.getSequenciaAtual());
        assertEquals(3, streak.getMelhorSequencia());
    }

    @Test
    void reconstruirDeveManterEstadoExato() {
        LocalDate data = LocalDate.of(2026, 3, 10);

        Streak streak = Streak.reconstruir(4, 6, data);

        assertEquals(4, streak.getSequenciaAtual());
        assertEquals(6, streak.getMelhorSequencia());
        assertEquals(data, streak.getDataUltimaAtividade());
    }
}