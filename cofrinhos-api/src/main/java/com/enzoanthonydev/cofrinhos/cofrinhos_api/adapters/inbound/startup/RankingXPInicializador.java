package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.startup;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.ReconstruirRankingXPUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class RankingXPInicializador implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RankingXPInicializador.class);

    private final ReconstruirRankingXPUseCase reconstruirRankingXPUseCase;

    public RankingXPInicializador ( ReconstruirRankingXPUseCase reconstruirRankingXPUseCase) {
        this.reconstruirRankingXPUseCase = reconstruirRankingXPUseCase;
    }
    @Override
    public void run (ApplicationArguments args) {
        try {
            int total = reconstruirRankingXPUseCase.executar();
            log.info("Ranking de XP reconstruído a partir do Postgres: {} perfil(is)", total);
        } catch ( RuntimeException e ) {
            log.warn("Não foi possível reconstruir o ranking de XP na inicialização", e);
        }
    }
}
