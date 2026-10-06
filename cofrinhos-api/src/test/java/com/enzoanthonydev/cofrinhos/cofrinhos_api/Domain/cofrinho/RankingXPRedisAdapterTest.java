package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence.redis.RankingXPRedisAdapter;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PosicaoRanking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class RankingXPRedisAdapterTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    private LettuceConnectionFactory connectionFactory;
    private RankingXPRedisAdapter adapter;

    @BeforeEach
    void prepararAdaptador() {
        connectionFactory = new LettuceConnectionFactory(redis.getHost(), redis.getMappedPort(6379));
        connectionFactory.afterPropertiesSet();

        StringRedisTemplate redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.delete("ranking:xp");

        adapter = new RankingXPRedisAdapter(redisTemplate);
    }

    @AfterEach
    void fecharConexao() {
        connectionFactory.destroy();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaNinguemNoRanking() {
        List<PosicaoRanking> ranking = adapter.buscarTop(10);

        assertTrue(ranking.isEmpty());
    }

    @Test
    void deveOrdenarPorXpDecrescenteComPosicoesSequenciais() {
        UUID usuarioA = UUID.randomUUID();
        UUID usuarioB = UUID.randomUUID();
        UUID usuarioC = UUID.randomUUID();
        adapter.atualizarXP(usuarioA, 25);
        adapter.atualizarXP(usuarioB, 65);
        adapter.atualizarXP(usuarioC, 35);

        List<PosicaoRanking> ranking = adapter.buscarTop(10);

        assertEquals(3, ranking.size());
        assertEquals(new PosicaoRanking(1, usuarioB, 65), ranking.get(0));
        assertEquals(new PosicaoRanking(2, usuarioC, 35), ranking.get(1));
        assertEquals(new PosicaoRanking(3, usuarioA, 25), ranking.get(2));
    }

    @Test
    void deveRespeitarOLimiteDeResultados() {
        UUID usuarioA = UUID.randomUUID();
        UUID usuarioB = UUID.randomUUID();
        UUID usuarioC = UUID.randomUUID();
        adapter.atualizarXP(usuarioA, 10);
        adapter.atualizarXP(usuarioB, 30);
        adapter.atualizarXP(usuarioC, 20);

        List<PosicaoRanking> ranking = adapter.buscarTop(2);

        assertEquals(2, ranking.size());
        assertEquals(usuarioB, ranking.get(0).usuarioId());
        assertEquals(usuarioC, ranking.get(1).usuarioId());
    }

    @Test
    void deveSubstituirOXpEnaoSomarAoAtualizarOMesmoUsuario() {
        UUID usuarioId = UUID.randomUUID();

        adapter.atualizarXP(usuarioId, 25);
        adapter.atualizarXP(usuarioId, 25);
        adapter.atualizarXP(usuarioId, 135);

        List<PosicaoRanking> ranking = adapter.buscarTop(10);

        assertEquals(1, ranking.size());
        assertEquals(new PosicaoRanking(1, usuarioId, 135), ranking.get(0));
    }

    @Test
    void deveRetornarPosicaoEXpDoUsuarioNoRanking() {
        UUID usuarioA = UUID.randomUUID();
        UUID usuarioB = UUID.randomUUID();
        UUID usuarioC = UUID.randomUUID();
        adapter.atualizarXP(usuarioA, 25);
        adapter.atualizarXP(usuarioB, 65);
        adapter.atualizarXP(usuarioC, 35);

        Optional<PosicaoRanking> posicao = adapter.buscarPosicao(usuarioC);

        assertTrue(posicao.isPresent());
        assertEquals(new PosicaoRanking(2, usuarioC, 35), posicao.get());
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoEstaNoRanking() {
        adapter.atualizarXP(UUID.randomUUID(), 50);

        Optional<PosicaoRanking> posicao = adapter.buscarPosicao(UUID.randomUUID());

        assertTrue(posicao.isEmpty());
    }
}