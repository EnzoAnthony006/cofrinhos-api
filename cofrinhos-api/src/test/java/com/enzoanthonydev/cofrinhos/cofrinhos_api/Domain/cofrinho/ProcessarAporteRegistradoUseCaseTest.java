package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.Streak;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PerfilXPRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.ProcessarAporteRegistradoUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.RankingXPRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessarAporteRegistradoUseCaseTest {

    @Mock
    private PerfilXPRepository perfilXPRepository;

    @Mock
    private RankingXPRepository rankingXPRepository;

    private ProcessarAporteRegistradoUseCase useCase() {
        return new ProcessarAporteRegistradoUseCase(perfilXPRepository, rankingXPRepository);
    }

    @Test
    void deveIniciarNovoPerfilEDarPrimeiroXpQuandoUsuarioAindaNaoTemPerfil() {
        UUID usuarioId = UUID.randomUUID();
        when(perfilXPRepository.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());

        AporteRegistradoEvent evento = new AporteRegistradoEvent(
                UUID.randomUUID(), UUID.randomUUID(), usuarioId,
                new BigDecimal("100"), LocalDateTime.of(2026, 1, 1, 10, 0),
                StatusCofrinho.ATIVO, CategoriaInvestimento.ECONOMIA
        );

        useCase().executar(evento);

        ArgumentCaptor<PerfilXP> captor = ArgumentCaptor.forClass(PerfilXP.class);
        verify(perfilXPRepository).salvar(captor.capture());

        PerfilXP perfilSalvo = captor.getValue();
        assertEquals(usuarioId, perfilSalvo.getUsuarioId());
        assertEquals(1, perfilSalvo.getStreakGlobal().getSequenciaAtual());
        assertEquals(25, perfilSalvo.getXpTotal());
        verify(rankingXPRepository).atualizarXP(usuarioId, 25);
    }

    @Test
    void deveAtualizarPerfilExistenteAcumulandoXpEStreak() {
        UUID usuarioId = UUID.randomUUID();
        Streak streakExistente = Streak.reconstruir(3, 3, LocalDate.of(2026, 1, 1));
        PerfilXP perfilExistente = PerfilXP.reconstruir(usuarioId, 100, streakExistente);

        when(perfilXPRepository.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.of(perfilExistente));

        AporteRegistradoEvent evento = new AporteRegistradoEvent(
                UUID.randomUUID(), UUID.randomUUID(), usuarioId,
                new BigDecimal("50"), LocalDateTime.of(2026, 1, 5, 9, 0),
                StatusCofrinho.ATIVO, CategoriaInvestimento.ECONOMIA
        );

        useCase().executar(evento);

        ArgumentCaptor<PerfilXP> captor = ArgumentCaptor.forClass(PerfilXP.class);
        verify(perfilXPRepository).salvar(captor.capture());

        PerfilXP perfilSalvo = captor.getValue();
        assertEquals(4, perfilSalvo.getStreakGlobal().getSequenciaAtual());
        assertEquals(135, perfilSalvo.getXpTotal());
        verify(rankingXPRepository).atualizarXP(usuarioId, 135);
    }

    @Test
    void naoDeveFalharQuandoAtualizacaoDoRankingLancarExcecao() {
        UUID usuarioId = UUID.randomUUID();
        when(perfilXPRepository.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());
        doThrow(new RuntimeException("redis indisponivel"))
                .when(rankingXPRepository).atualizarXP(any(UUID.class), anyInt());

        AporteRegistradoEvent evento = new AporteRegistradoEvent(
                UUID.randomUUID(), UUID.randomUUID(), usuarioId,
                new BigDecimal("100"), LocalDateTime.of(2026, 1, 1, 10, 0),
                StatusCofrinho.ATIVO, CategoriaInvestimento.ECONOMIA
        );

        assertDoesNotThrow(() -> useCase().executar(evento));

        verify(perfilXPRepository).salvar(any(PerfilXP.class));
    }
}
