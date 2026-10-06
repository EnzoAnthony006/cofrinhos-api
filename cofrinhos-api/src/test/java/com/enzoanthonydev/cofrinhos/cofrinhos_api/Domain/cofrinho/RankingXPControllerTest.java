package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest.RankingXPController;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarRankingXPUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PosicaoRanking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RankingXPControllerTest {

    @Mock
    private BuscarRankingXPUseCase buscarRankingXPUseCase;

    @Test
    void deveEmitirNovoRankingSomenteQuandoOResultadoMudar() {
        UUID usuarioA = UUID.randomUUID();
        UUID usuarioB = UUID.randomUUID();
        List<PosicaoRanking> primeiro = List.of(new PosicaoRanking(1, usuarioA, 65));
        List<PosicaoRanking> segundo = List.of(
                new PosicaoRanking(1, usuarioB, 90),
                new PosicaoRanking(2, usuarioA, 65));

        when(buscarRankingXPUseCase.executar(10)).thenReturn(primeiro, primeiro, segundo);

        RankingXPController controller = new RankingXPController(buscarRankingXPUseCase);

        StepVerifier.create(controller.acompanharRanking(10).take(2))
                .expectNext(primeiro)
                .expectNext(segundo)
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }
}