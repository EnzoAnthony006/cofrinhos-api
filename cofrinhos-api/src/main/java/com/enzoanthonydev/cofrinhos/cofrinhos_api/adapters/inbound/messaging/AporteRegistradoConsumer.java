package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.messaging;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.ProcessarAporteRegistradoUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AporteRegistradoConsumer {

    private final ProcessarAporteRegistradoUseCase processarAporteRegistradoUseCase;

    public AporteRegistradoConsumer(ProcessarAporteRegistradoUseCase processarAporteRegistradoUseCase) {
        this.processarAporteRegistradoUseCase = processarAporteRegistradoUseCase;
    }

    @KafkaListener(topics = "aporte-registrado", groupId = "cofrinhos-api")
    public void consumir(AporteRegistradoEvent evento) {
        processarAporteRegistradoUseCase.executar(evento);
    }
}
