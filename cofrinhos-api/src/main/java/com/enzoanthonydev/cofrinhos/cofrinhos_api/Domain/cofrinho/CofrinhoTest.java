package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

class CofrinhoTest {

    private final UUID usuarioId = UUID.randomUUID();

    @Test
    void deveCriarCofrinhoAtivoComValorAcumuladoZero() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Viagem dos sonhos", "Europa em 2027",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("5000")));

        assertEquals(StatusCofrinho.ATIVO, cofrinho.getStatus());
        assertEquals(Dinheiro.zero(), cofrinho.getValorAcumulado());
    }

    @Test
    void naoDeveCriarCofrinhoComNomeVazio() {
        assertThrows(IllegalArgumentException.class, () ->
                Cofrinho.criar(usuarioId, "", "descricao",
                        CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("100"))));
    }

    @Test
    void naoDeveCriarCofrinhoComMetaZeroOuNegativa() {
        assertThrows(IllegalArgumentException.class, () ->
                Cofrinho.criar(usuarioId, "Reserva", "descricao",
                        CategoriaInvestimento.RESERVA_EMERGENCIA, Dinheiro.de(BigDecimal.ZERO)));
    }

    @Test
    void deveSomarValorAoRegistrarAporte() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Carro novo", "descricao",
                CategoriaInvestimento.RENDA_FIXA, Dinheiro.de(new BigDecimal("10000")));

        cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("500")));

        assertEquals(Dinheiro.de(new BigDecimal("500")), cofrinho.getValorAcumulado());
    }

    @Test
    void deveConcluirCofrinhoQuandoAtingeAMeta() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Reserva de emergência", "descricao",
                CategoriaInvestimento.RESERVA_EMERGENCIA, Dinheiro.de(new BigDecimal("1000")));

        cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("1000")));

        assertEquals(StatusCofrinho.CONCLUIDO, cofrinho.getStatus());
    }

    @Test
    void naoDevePermitirAporteEmCofrinhoArquivado() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Cofrinho antigo", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("100")));
        cofrinho.arquivar();

        assertThrows(IllegalStateException.class, () ->
                cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("10"))));
    }
}