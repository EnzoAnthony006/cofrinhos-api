package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CofrinhoTest {

    private final UUID usuarioId = UUID.randomUUID();

    @Test
    void deveCriarCofrinhoComValoresIniciais() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));

        assertNotNull(cofrinho.getId());
        assertEquals(usuarioId, cofrinho.getUsuarioId());
        assertEquals("Viagem", cofrinho.getNome());
        assertEquals(Dinheiro.zero(), cofrinho.getValorAcumulado());
        assertEquals(StatusCofrinho.ATIVO, cofrinho.getStatus());
    }

    @Test
    void naoDeveCriarCofrinhoComNomeVazio() {
        assertThrows(IllegalArgumentException.class, () ->
                Cofrinho.criar(usuarioId, "", "descricao",
                        CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000"))));
    }

    @Test
    void naoDeveCriarCofrinhoComCategoriaNula() {
        assertThrows(IllegalArgumentException.class, () ->
                Cofrinho.criar(usuarioId, "Viagem", "descricao",
                        null, Dinheiro.de(new BigDecimal("1000"))));
    }

    @Test
    void naoDeveCriarCofrinhoComValorMetaZero() {
        assertThrows(IllegalArgumentException.class, () ->
                Cofrinho.criar(usuarioId, "Viagem", "descricao",
                        CategoriaInvestimento.ECONOMIA, Dinheiro.zero()));
    }

    @Test
    void deveAcumularValorSemConcluirQuandoAporteNaoAtingeMeta() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));

        cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("250")));

        assertEquals(Dinheiro.de(new BigDecimal("250")), cofrinho.getValorAcumulado());
        assertEquals(StatusCofrinho.ATIVO, cofrinho.getStatus());
    }

    @Test
    void deveConcluirCofrinhoQuandoAporteAtingeExatamenteAMeta() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));

        cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("1000")));

        assertEquals(StatusCofrinho.CONCLUIDO, cofrinho.getStatus());
    }

    @Test
    void deveConcluirCofrinhoQuandoAporteUltrapassaAMeta() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));

        cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("1500")));

        assertEquals(StatusCofrinho.CONCLUIDO, cofrinho.getStatus());
    }

    @Test
    void naoDevePermitirAporteEmCofrinhoArquivado() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));
        cofrinho.arquivar();

        assertThrows(IllegalStateException.class, () ->
                cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("100"))));
    }

    @Test
    void deveCalcularProgressoCorretamente() {
        Cofrinho cofrinho = Cofrinho.criar(usuarioId, "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));

        cofrinho.registrarAporte(Dinheiro.de(new BigDecimal("250")));

        assertEquals(25.0, cofrinho.progresso(), 0.001);
    }

    @Test
    void reconstruirDeveManterEstadoExato() {
        UUID id = UUID.randomUUID();
        Cofrinho cofrinho = Cofrinho.reconstruir(id, usuarioId, "Reserva", "descricao",
                CategoriaInvestimento.RESERVA_EMERGENCIA, Dinheiro.de(new BigDecimal("500")),
                Dinheiro.de(new BigDecimal("500")), StatusCofrinho.CONCLUIDO);

        assertEquals(id, cofrinho.getId());
        assertEquals(StatusCofrinho.CONCLUIDO, cofrinho.getStatus());
        assertEquals(Dinheiro.de(new BigDecimal("500")), cofrinho.getValorAcumulado());
    }
}