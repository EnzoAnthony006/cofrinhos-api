package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CriarCofrinhoRequest (
        @NotNull UUID usuarioId,
        @NotBlank String nome,
        String descricao,
        @NotNull CategoriaInvestimento categoria,
        @NotNull @Positive BigDecimal valorMeta
        ) {
}
