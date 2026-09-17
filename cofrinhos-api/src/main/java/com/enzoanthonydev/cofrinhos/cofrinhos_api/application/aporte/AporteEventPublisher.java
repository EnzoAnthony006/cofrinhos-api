package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte;

public interface AporteEventPublisher {

    void publicar(AporteRegistradoEvent evento);
}
