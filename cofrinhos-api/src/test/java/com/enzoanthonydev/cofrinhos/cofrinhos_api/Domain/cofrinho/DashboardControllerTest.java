package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.Streak;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest.DashboardController;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarConquistasUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarPerfilXPUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarPosicaoNoRankingUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PosicaoRanking;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private BuscarPerfilXPUseCase buscarPerfilXPUseCase;

    @Mock
    private BuscarConquistasUseCase buscarConquistasUseCase;

    @Mock
    private BuscarPosicaoNoRankingUseCase buscarPosicaoNoRankingUseCase;

    private DashboardController controller() {
        return new DashboardController(buscarPerfilXPUseCase, buscarConquistasUseCase, buscarPosicaoNoRankingUseCase);
    }

    @Test
    void deveMontarDashboardComPerfilConquistasEPosicaoNoRanking() {
        UUID usuarioId = UUID.randomUUID();
        PerfilXP perfil = PerfilXP.reconstruir(usuarioId, 150, Streak.reconstruir(3, 5, LocalDate.of(2026, 9, 28)));
        ConquistaDesbloqueada conquista = ConquistaDesbloqueada.reconstruir(
                UUID.randomUUID(), usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO,
                LocalDateTime.of(2026, 9, 28, 10, 0));
        PosicaoRanking posicao = new PosicaoRanking(2, usuarioId, 150);

        when(buscarPerfilXPUseCase.buscarSeExistir(usuarioId)).thenReturn(Optional.of(perfil));
        when(buscarConquistasUseCase.executar(usuarioId)).thenReturn(List.of(conquista));
        when(buscarPosicaoNoRankingUseCase.executar(usuarioId)).thenReturn(Optional.of(posicao));

        StepVerifier.create(controller().buscar(usuarioId))
                .assertNext(dashboard -> {
                    assertEquals(usuarioId, dashboard.usuarioId());
                    assertEquals(150, dashboard.perfil().xpTotal());
                    assertEquals(3, dashboard.perfil().sequenciaAtual());
                    assertEquals(1, dashboard.conquistas().size());
                    assertEquals(DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO, dashboard.conquistas().get(0).definicao());
                    assertEquals(posicao, dashboard.posicaoNoRanking());
                })
                .verifyComplete();
    }

    @Test
    void deveDevolverDashboardZeradoParaUsuarioSemPerfilConquistasOuRanking() {
        UUID usuarioId = UUID.randomUUID();

        when(buscarPerfilXPUseCase.buscarSeExistir(usuarioId)).thenReturn(Optional.empty());
        when(buscarConquistasUseCase.executar(usuarioId)).thenReturn(List.of());
        when(buscarPosicaoNoRankingUseCase.executar(usuarioId)).thenReturn(Optional.empty());

        StepVerifier.create(controller().buscar(usuarioId))
                .assertNext(dashboard -> {
                    assertEquals(usuarioId, dashboard.perfil().usuarioId());
                    assertEquals(0, dashboard.perfil().xpTotal());
                    assertEquals(0, dashboard.perfil().sequenciaAtual());
                    assertTrue(dashboard.conquistas().isEmpty());
                    assertNull(dashboard.posicaoNoRanking());
                })
                .verifyComplete();
    }
}
