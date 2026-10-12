package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoResumo;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.ListarCofrinhosDoUsuarioUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarCofrinhosDoUsuarioUseCaseTest {

    @Mock
    private CofrinhoRepository cofrinhoRepository;

    private Cofrinho cofrinhoDe(UUID usuarioId, String nome) {
        return Cofrinho.criar(usuarioId, nome, "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));
    }

    @Test
    void deveListarCofrinhosDoUsuarioPreservandoAOrdemDoRepositorio() {
        UUID usuarioId = UUID.randomUUID();
        when(cofrinhoRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(List.of(cofrinhoDe(usuarioId, "Carro"), cofrinhoDe(usuarioId, "Viagem")));

        List<CofrinhoResumo> resultado = new ListarCofrinhosDoUsuarioUseCase(cofrinhoRepository)
                .executar(usuarioId);

        assertEquals(List.of("Carro", "Viagem"), resultado.stream().map(CofrinhoResumo::nome).toList());
        assertTrue(resultado.stream().allMatch(r -> r.usuarioId().equals(usuarioId)));
        verify(cofrinhoRepository).buscarPorUsuarioId(usuarioId);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemCofrinhos() {
        UUID usuarioId = UUID.randomUUID();
        when(cofrinhoRepository.buscarPorUsuarioId(usuarioId)).thenReturn(List.of());

        List<CofrinhoResumo> resultado = new ListarCofrinhosDoUsuarioUseCase(cofrinhoRepository)
                .executar(usuarioId);

        assertTrue(resultado.isEmpty());
    }
}
