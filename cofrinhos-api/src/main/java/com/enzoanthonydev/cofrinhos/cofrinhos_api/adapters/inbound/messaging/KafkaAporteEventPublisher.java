package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.messaging;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteEventPublisher;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaAporteEventPublisher implements AporteEventPublisher {

    private static final String TOPICO = "aporte-registrado";

    private final KafkaTemplate<String, AporteRegistradoEvent> kafkaTemplate;


    public KafkaAporteEventPublisher(KafkaTemplate<String, AporteRegistradoEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public void publicar(AporteRegistradoEvent event) {
        kafkaTemplate.send(TOPICO, event.cofrinhoId().toString(), event);
    }

}
