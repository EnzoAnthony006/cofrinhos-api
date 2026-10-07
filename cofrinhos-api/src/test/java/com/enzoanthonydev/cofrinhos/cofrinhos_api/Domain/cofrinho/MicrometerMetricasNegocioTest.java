package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.outbound.metrics.MicrometerMetricasNegocio;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MicrometerMetricasNegocioTest {

    private SimpleMeterRegistry registry;
    private MicrometerMetricasNegocio metricas;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metricas = new MicrometerMetricasNegocio(registry);
    }

    @Test
    void deveContarAportesSeparadosPorCategoria() {
        metricas.aporteRegistrado("VIAGEM");
        metricas.aporteRegistrado("VIAGEM");
        metricas.aporteRegistrado("RESERVA");

        assertEquals(2.0, registry.get("cofrinhos.aportes.registrados")
                .tag("categoria", "VIAGEM").counter().count());
        assertEquals(1.0, registry.get("cofrinhos.aportes.registrados")
                .tag("categoria", "RESERVA").counter().count());
    }

    @Test
    void deveSomarOXPConcedido() {
        metricas.xpConcedido(50);
        metricas.xpConcedido(15);

        assertEquals(65.0, registry.get("cofrinhos.xp.concedido").counter().count());
    }

    @Test
    void deveContarConquistasPorTipo() {
        metricas.conquistaDesbloqueada("PRIMEIRO_COFRINHO_CONCLUIDO");
        metricas.conquistaDesbloqueada("PRIMEIRO_COFRINHO_CONCLUIDO");

        assertEquals(2.0, registry.get("cofrinhos.conquistas.desbloqueadas")
                .tag("conquista", "PRIMEIRO_COFRINHO_CONCLUIDO").counter().count());
    }

    @Test
    void deveContarFalhasDoRanking() {
        metricas.falhaAoAtualizarRanking();

        assertEquals(1.0, registry.get("cofrinhos.ranking.falhas").counter().count());
    }
}
