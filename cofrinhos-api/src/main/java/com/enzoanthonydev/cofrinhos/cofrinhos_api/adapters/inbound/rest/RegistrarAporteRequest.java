package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record RegistrarAporteRequest (@NotNull @Positive BigDecimal valor

                                      ) {
}
