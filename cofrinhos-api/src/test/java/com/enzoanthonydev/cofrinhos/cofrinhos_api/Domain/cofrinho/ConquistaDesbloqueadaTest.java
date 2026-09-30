package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConquistaDesbloqueadaTest {

    private final UUID usuarioId = UUID.randomUUID();

    @Test
    void deveDesbloquearComIdEDataGerados() {
        ConquistaDesbloqueada conquista = ConquistaDesbloqueada.desbloquear(usuarioId, DefinicaoConquista.STREAK_QUATRO_SEMANAS);

        assertNotNull(conquista.getId());
        assertEquals(usuarioId, conquista.getUsuarioId());
        assertEquals(DefinicaoConquista.STREAK_QUATRO_SEMANAS, conquista.getDefinicao());
        assertNotNull(conquista.getDataDesbloqueio());
    }

    @Test
    void reconstruirDeveManterEstadoExato() {
        UUID id = UUID.randomUUID();
        LocalDateTime data = LocalDateTime.of(2026, 1, 1, 10, 0);

        ConquistaDesbloqueada conquista = ConquistaDesbloqueada.reconstruir(
                id, usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO, data);

        assertEquals(id, conquista.getId());
        assertEquals(usuarioId, conquista.getUsuarioId());
        assertEquals(DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO, conquista.getDefinicao());
        assertEquals(data, conquista.getDataDesbloqueio());
    }

    @Test
    void deveConsiderarIguaisQuandoIdForOMesmo() {
        UUID id = UUID.randomUUID();
        ConquistaDesbloqueada conquista1 = ConquistaDesbloqueada.reconstruir(
                id, usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO, LocalDateTime.now());
        ConquistaDesbloqueada conquista2 = ConquistaDesbloqueada.reconstruir(
                id, UUID.randomUUID(), DefinicaoConquista.STREAK_QUATRO_SEMANAS, LocalDateTime.now().plusDays(1));

        assertEquals(conquista1, conquista2);
        assertEquals(conquista1.hashCode(), conquista2.hashCode());
    }

    @Test
    void naoDeveConsiderarIguaisQuandoIdForDiferente() {
        ConquistaDesbloqueada conquista1 = ConquistaDesbloqueada.desbloquear(usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO);
        ConquistaDesbloqueada conquista2 = ConquistaDesbloqueada.desbloquear(usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO);

        assertNotEquals(conquista1, conquista2);
    }
}