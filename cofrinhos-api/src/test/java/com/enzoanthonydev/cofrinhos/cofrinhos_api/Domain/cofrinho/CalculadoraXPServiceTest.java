package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.CalculadoraXPService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculadoraXPServiceTest {

    private final CalculadoraXPService calculadoraXPService = new CalculadoraXPService();

    @Test
    void deveCalcularXpBaseMaisProporcionalSemStreak() {
        int xp = calculadoraXPService.calcular(Dinheiro.de(new BigDecimal("50")), 0);

        assertEquals(15, xp);
    }

    @Test
    void deveTruncarXpProporcionalParaBaixo() {
        int xp = calculadoraXPService.calcular(Dinheiro.de(new BigDecimal("99")), 0);

        assertEquals(19, xp);
    }

    @Test
    void deveRespeitarTetoDoXpProporcional() {
        int xp = calculadoraXPService.calcular(Dinheiro.de(new BigDecimal("10000")), 0);

        assertEquals(60, xp);
    }

    @Test
    void deveSomarBonusDeStreak() {
        int xp = calculadoraXPService.calcular(Dinheiro.de(new BigDecimal("100")), 4);

        assertEquals(40, xp);
    }
}
