package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.startup.RankingXPInicializador;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.ReconstruirRankingXPUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingXPInicializadorTest {

    @Mock
    private ReconstruirRankingXPUseCase reconstruirRankingXPUseCase;

    @InjectMocks
    private RankingXPInicializador inicializador;

    @Test
    void naoDeveFalharAInicializacaoQuandoAReconstrucaoLancarExcecao() {
        when(reconstruirRankingXPUseCase.executar()).thenThrow(new RuntimeException("redis indisponivel"));

        assertDoesNotThrow(() -> inicializador.run(null));
    }
}
