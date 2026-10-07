package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.metrics;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.metrics.MetricasNegocio;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MicrometerMetricasNegocio implements MetricasNegocio {

    private final MeterRegistry registry;

    public MicrometerMetricasNegocio(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void aporteRegistrado(String categoria) {
        Counter.builder("cofrinhos.aportes.registrados")
                .description("Aportes registrados, por categoria do cofrinho")
                .tag("categoria", categoria)
                .register(registry)
                .increment();
    }

    @Override
    public void xpConcedido(int xp) {
        Counter.builder("cofrinhos.xp.concedido")
                .description("Total de XP concedido aos usuários")
                .register(registry)
                .increment(xp);
    }

    @Override
    public void conquistaDesbloqueada(String conquista) {
        Counter.builder("cofrinhos.conquistas.desbloqueadas")
                .description("Conquistas desbloqueadas, por tipo")
                .tag("conquista", conquista)
                .register(registry)
                .increment();
    }

    @Override
    public void falhaAoAtualizarRanking() {
        Counter.builder("cofrinhos.ranking.falhas")
                .description("Falhas ao atualizar o ranking de XP no Redis")
                .register(registry)
                .increment();
    }
}
