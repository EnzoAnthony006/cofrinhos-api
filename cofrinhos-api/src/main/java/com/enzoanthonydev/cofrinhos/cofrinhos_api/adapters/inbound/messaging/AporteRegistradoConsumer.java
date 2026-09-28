package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.messaging;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.ProcessarAporteRegistradoUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.VerificarConquistasUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AporteRegistradoConsumer {

    private final ProcessarAporteRegistradoUseCase processarAporteRegistradoUseCase;
    private final VerificarConquistasUseCase verificarConquistasUseCase;

    public AporteRegistradoConsumer(ProcessarAporteRegistradoUseCase processarAporteRegistradoUseCase,
                                    VerificarConquistasUseCase verificarConquistasUseCase) {
        this.processarAporteRegistradoUseCase = processarAporteRegistradoUseCase;
        this.verificarConquistasUseCase = verificarConquistasUseCase;
    }

    @KafkaListener(topics = "aporte-registrado", groupId = "cofrinhos-api")
    public void consumir(AporteRegistradoEvent evento) {
        processarAporteRegistradoUseCase.executar(evento);
        verificarConquistasUseCase.executar(evento);
    }
}