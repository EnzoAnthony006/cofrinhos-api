package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.Streak;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PerfilXPRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.RankingXPRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.ReconstruirRankingXPUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReconstruirRankingXPUseCaseTest {

    @Mock
    private PerfilXPRepository perfilXPRepository;

    @Mock
    private RankingXPRepository rankingXPRepository;

    @InjectMocks
    private ReconstruirRankingXPUseCase useCase;

    @Test
    void deveRegravarOXPDeTodosOsPerfisNoRanking() {
        UUID usuarioA = UUID.randomUUID();
        UUID usuarioB = UUID.randomUUID();
        Streak streak = Streak.reconstruir(1, 1, LocalDate.of(2026, 10, 5));

        when(perfilXPRepository.buscarTodos()).thenReturn(List.of(
                PerfilXP.reconstruir(usuarioA, 65, streak),
                PerfilXP.reconstruir(usuarioB, 35, streak)
        ));

        int total = useCase.executar();

        assertEquals(2, total);
        verify(rankingXPRepository).atualizarXP(usuarioA, 65);
        verify(rankingXPRepository).atualizarXP(usuarioB, 35);
    }

    @Test
    void naoDeveTocarNoRankingQuandoNaoHaPerfis() {
        when(perfilXPRepository.buscarTodos()).thenReturn(List.of());

        int total = useCase.executar();

        assertEquals(0, total);
        verifyNoInteractions(rankingXPRepository);
    }
}