package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record AporteRegistradoEvent(
        UUID aporteId,
        UUID cofrinhoId,
        UUID usuarioId,
        BigDecimal valor,
        LocalDateTime dataRegistro,
        StatusCofrinho statusCofrinho,
        CategoriaInvestimento categoriaCofrinho
) {
}
