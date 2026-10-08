package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.infrastructure.TracingConfig;
import io.micrometer.tracing.exporter.FinishedSpan;
import io.micrometer.tracing.exporter.SpanExportingPredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TracingConfigTest {

    private final SpanExportingPredicate predicate = new TracingConfig().ignorarEndpointsDoActuator();

    @Mock
    private FinishedSpan span;

    @Test
    void naoDeveExportarSpanDeEndpointDoActuator() {
        when(span.getName()).thenReturn("http get /actuator/prometheus");

        assertFalse(predicate.isExportable(span));
    }

    @Test
    void deveExportarSpanDeEndpointDeNegocio() {
        when(span.getName()).thenReturn("http get /ranking");

        assertTrue(predicate.isExportable(span));
    }

    @Test
    void deveExportarSpanSemNome() {
        when(span.getName()).thenReturn(null);

        assertTrue(predicate.isExportable(span));
    }
}
