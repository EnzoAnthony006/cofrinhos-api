package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CriarCofrinhoUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CriarCofrinhoUseCaseTest {

    @Mock
    private CofrinhoRepository cofrinhoRepository;

    @Test
    void deveCriarESalvarCofrinho() {
        CriarCofrinhoUseCase useCase = new CriarCofrinhoUseCase(cofrinhoRepository);
        UUID usuarioId = UUID.randomUUID();

        when(cofrinhoRepository.salvar(any(Cofrinho.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Cofrinho resultado = useCase.executar(usuarioId, "Viagem", "descricao",
                CategoriaInvestimento.ECONOMIA, Dinheiro.de(new BigDecimal("1000")));

        assertEquals(usuarioId, resultado.getUsuarioId());
        verify(cofrinhoRepository).salvar(any(Cofrinho.class));
    }
}
