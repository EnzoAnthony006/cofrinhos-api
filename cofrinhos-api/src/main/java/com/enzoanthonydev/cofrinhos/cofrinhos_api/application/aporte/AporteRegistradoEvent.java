package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AporteRegistradoEvent(
        UUID aporteId,
        UUID cofrinhoId,
        UUID usuarioId,
        BigDecimal valor,
        LocalDateTime dataRegistro
) {
}
