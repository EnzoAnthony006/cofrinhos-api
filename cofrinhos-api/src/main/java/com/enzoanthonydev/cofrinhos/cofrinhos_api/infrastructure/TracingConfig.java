package com.enzoanthonydev.cofrinhos.cofrinhos_api.infrastructure;

import io.micrometer.tracing.exporter.SpanExportingPredicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TracingConfig {

    @Bean
    public SpanExportingPredicate ignorarEndpointsDoActuator() {
        return span -> span.getName() == null || !span.getName().contains("/actuator");
    }
}
