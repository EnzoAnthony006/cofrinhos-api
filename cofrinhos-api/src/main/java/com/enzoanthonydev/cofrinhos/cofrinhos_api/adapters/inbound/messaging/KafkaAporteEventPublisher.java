package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.messaging;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteEventPublisher;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaAporteEventPublisher implements AporteEventPublisher {

    private static final String TOPICO = "aporte-registrado";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaAporteEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publicar(AporteRegistradoEvent evento) {
        kafkaTemplate.send(TOPICO, evento.cofrinhoId().toString(), evento);
    }
}