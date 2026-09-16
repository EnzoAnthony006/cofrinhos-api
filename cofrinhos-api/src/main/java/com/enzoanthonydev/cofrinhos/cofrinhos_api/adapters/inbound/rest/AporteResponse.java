package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte.Aporte;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AporteResponse(
        UUID id,
        UUID cofrinhoID,
        BigDecimal valor,
        LocalDateTime dataRegistro
) {
    public static AporteResponse de(Aporte aporte) {
        return new AporteResponse(
                aporte.getId(),
                aporte.getCofrinhoId(),
                aporte.getValor().valor(),
                aporte.getDataRegistro()
        );
    }
}
