package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoResumo;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.ListarCofrinhosDoUsuarioUseCase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListarCofrinhosDoUsuarioUseCaseTest {

    private final CofrinhoRepository cofrinhoRepository = mock(CofrinhoRepository.class);
    private final ListarCofrinhosDoUsuarioUseCase useCase =
            new ListarCofrinhosDoUsuarioUseCase(cofrinhoRepository);

    @Test
    void deveListarCofrinhosDoUsuarioConsultandoApenasPeloIdDele() {
        UUID usuarioId = UUID.randomUUID();
        List<Cofrinho> cofrinhos = List.of(
                Cofrinho.criar(usuarioId, "Carro", "descricao",
                        CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("5000"))),
                Cofrinho.criar(usuarioId, "Viagem", "descricao",
                        CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")))
        );
        when(cofrinhoRepository.buscarPorUsuarioId(usuarioId)).thenReturn(cofrinhos);

        List<CofrinhoResumo> resultado = useCase.executar(usuarioId);

        assertEquals(2, resultado.size());
        verify(cofrinhoRepository).buscarPorUsuarioId(usuarioId);
    }

    @Test
    void deveRetornarListaVaziaQuandoUsuarioNaoTemCofrinhos() {
        UUID usuarioId = UUID.randomUUID();
        when(cofrinhoRepository.buscarPorUsuarioId(usuarioId)).thenReturn(List.of());

        List<CofrinhoResumo> resultado = useCase.executar(usuarioId);

        assertTrue(resultado.isEmpty());
    }
}
