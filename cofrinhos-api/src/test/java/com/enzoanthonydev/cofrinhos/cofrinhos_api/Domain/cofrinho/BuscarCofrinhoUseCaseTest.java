package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.BuscarCofrinhoUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoNaoEncontradoException;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoResumo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarCofrinhoUseCaseTest {

    @Mock
    private CofrinhoRepository cofrinhoRepository;

    private Cofrinho cofrinhoDe(UUID usuarioId) {
        return Cofrinho.criar(usuarioId, "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));
    }

    @Test
    void deveRetornarResumoQuandoUsuarioEhDono() {
        UUID id = UUID.randomUUID();
        UUID usuarioId = UUID.randomUUID();
        when(cofrinhoRepository.buscarPorId(id)).thenReturn(Optional.of(cofrinhoDe(usuarioId)));

        CofrinhoResumo resumo = new BuscarCofrinhoUseCase(cofrinhoRepository).executar(id, usuarioId);

        assertEquals(usuarioId, resumo.usuarioId());
        assertEquals("Viagem", resumo.nome());
    }

    @Test
    void deveLancarNaoEncontradoQuandoCofrinhoEhDeOutroUsuario() {
        UUID id = UUID.randomUUID();
        UUID dono = UUID.randomUUID();
        UUID intruso = UUID.randomUUID();
        when(cofrinhoRepository.buscarPorId(id)).thenReturn(Optional.of(cofrinhoDe(dono)));

        assertThrows(CofrinhoNaoEncontradoException.class, () ->
                new BuscarCofrinhoUseCase(cofrinhoRepository).executar(id, intruso));
    }

    @Test
    void deveLancarNaoEncontradoQuandoCofrinhoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(cofrinhoRepository.buscarPorId(id)).thenReturn(Optional.empty());

        assertThrows(CofrinhoNaoEncontradoException.class, () ->
                new BuscarCofrinhoUseCase(cofrinhoRepository).executar(id, UUID.randomUUID()));
    }
}
