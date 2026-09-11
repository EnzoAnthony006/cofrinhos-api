package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte.Aporte;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AporteTest {

    private final UUID cofrinhoId = UUID.randomUUID();

    @Test
    void deveRegistrarAporteComDataPreenchida() {
        Dinheiro valor = Dinheiro.de(new BigDecimal("250"));

        Aporte aporte = Aporte.registrar(cofrinhoId, valor);

        assertEquals(cofrinhoId, aporte.getCofrinhoId());
        assertEquals(valor, aporte.getValor());
        assertNotNull(aporte.getDataRegistro());
        assertNotNull(aporte.getId());
    }

    @Test
    void naoDeveRegistrarAporteComCofrinhoIdNulo() {
        assertThrows(IllegalArgumentException.class, () ->
                Aporte.registrar(null, Dinheiro.de(new BigDecimal("100"))));
    }

    @Test
    void naoDeveRegistrarAporteComValorZero() {
        assertThrows(IllegalArgumentException.class, () ->
                Aporte.registrar(cofrinhoId, Dinheiro.zero()));
    }

    @Test
    void naoDeveRegistrarAporteComValorNulo() {
        assertThrows(IllegalArgumentException.class, () ->
                Aporte.registrar(cofrinhoId, null));
    }
}