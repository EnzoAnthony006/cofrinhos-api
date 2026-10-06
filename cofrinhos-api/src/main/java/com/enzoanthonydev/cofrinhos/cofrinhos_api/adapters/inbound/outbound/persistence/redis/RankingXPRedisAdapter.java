package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.persistence.redis;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PosicaoRanking;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.RankingXPRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
public class RankingXPRedisAdapter implements RankingXPRepository {

    private static final String CHAVE_RANKING = "ranking:xp";

    private final StringRedisTemplate redisTemplate;

    public RankingXPRedisAdapter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void atualizarXP(UUID usuarioId, int xpTotal) {
        redisTemplate.opsForZSet().add(CHAVE_RANKING, usuarioId.toString(), xpTotal);
    }

    @Override
    public List<PosicaoRanking> buscarTop(int limite) {
        Set<ZSetOperations.TypedTuple<String>> tuplas =
                redisTemplate.opsForZSet().reverseRangeWithScores(CHAVE_RANKING, 0, limite - 1);

        if (tuplas == null) {
            return List.of();
        }

        List<PosicaoRanking> ranking = new ArrayList<>();
        int posicao = 1;
        for (ZSetOperations.TypedTuple<String> tupla : tuplas) {
            ranking.add(new PosicaoRanking(
                    posicao++,
                    UUID.fromString(tupla.getValue()),
                    tupla.getScore().intValue()
            ));
        }
        return ranking;
    }

    @Override
    public Optional<PosicaoRanking> buscarPosicao(UUID usuarioId) {
        String membro = usuarioId.toString();
        Long indice = redisTemplate.opsForZSet().reverseRank(CHAVE_RANKING, membro);
        Double score = redisTemplate.opsForZSet().score(CHAVE_RANKING, membro);

        if (indice == null || score == null) {
            return Optional.empty();
        }
        return Optional.of(new PosicaoRanking(indice.intValue() + 1, usuarioId, score.intValue()));
    }
}


