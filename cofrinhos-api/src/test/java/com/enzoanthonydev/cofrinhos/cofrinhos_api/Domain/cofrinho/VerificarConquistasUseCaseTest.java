package com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.cofrinho;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.Streak;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.ConquistaRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PerfilXPRepository;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.VerificarConquistasUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificarConquistasUseCaseTest {

    @Mock
    private ConquistaRepository conquistaRepository;

    @Mock
    private PerfilXPRepository perfilXPRepository;

    private VerificarConquistasUseCase useCase() {
        return new VerificarConquistasUseCase(conquistaRepository, perfilXPRepository);
    }

    private AporteRegistradoEvent evento(UUID usuarioId, StatusCofrinho status, CategoriaInvestimento categoria) {
        return new AporteRegistradoEvent(
                UUID.randomUUID(), UUID.randomUUID(), usuarioId,
                new BigDecimal("100"), LocalDateTime.now(), status, categoria
        );
    }

    private PerfilXP perfilComStreak(UUID usuarioId, int sequenciaAtual) {
        Streak streak = Streak.reconstruir(sequenciaAtual, sequenciaAtual, LocalDate.now());
        return PerfilXP.reconstruir(usuarioId, 0, streak);
    }

    @Test
    void deveDesbloquearPrimeiroCofrinhoConcluidoQuandoStatusConcluido() {
        UUID usuarioId = UUID.randomUUID();
        when(perfilXPRepository.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());

        useCase().executar(evento(usuarioId, StatusCofrinho.CONCLUIDO, CategoriaInvestimento.ECONOMIA));

        ArgumentCaptor<ConquistaDesbloqueada> captor = ArgumentCaptor.forClass(ConquistaDesbloqueada.class);
        verify(conquistaRepository, times(1)).salvar(captor.capture());
        assertEquals(DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO, captor.getValue().getDefinicao());
    }

    @Test
    void naoDeveDesbloquearNadaQuandoStatusNaoEhConcluidoENaoHaStreak() {
        UUID usuarioId = UUID.randomUUID();
        when(perfilXPRepository.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());

        useCase().executar(evento(usuarioId, StatusCofrinho.ATIVO, CategoriaInvestimento.ECONOMIA));

        verify(conquistaRepository, never()).salvar(any());
    }

    @Test
    void deveDesbloquearAmbasConquistasQuandoReservaEmergenciaConcluida() {
        UUID usuarioId = UUID.randomUUID();
        when(perfilXPRepository.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());

        useCase().executar(evento(usuarioId, StatusCofrinho.CONCLUIDO, CategoriaInvestimento.RESERVA_EMERGENCIA));

        ArgumentCaptor<ConquistaDesbloqueada> captor = ArgumentCaptor.forClass(ConquistaDesbloqueada.class);
        verify(conquistaRepository, times(2)).salvar(captor.capture());

        List<DefinicaoConquista> desbloqueadas = captor.getAllValues().stream()
                .map(ConquistaDesbloqueada::getDefinicao)
                .toList();

        assertTrue(desbloqueadas.contains(DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO));
        assertTrue(desbloqueadas.contains(DefinicaoConquista.PRIMEIRA_RESERVA_EMERGENCIA_COMPLETA));
    }

    @Test
    void naoDeveDesbloquearDeNovoQuandoConquistaJaExiste() {
        UUID usuarioId = UUID.randomUUID();
        when(perfilXPRepository.buscarPorUsuarioId(usuarioId)).thenReturn(Optional.empty());
        when(conquistaRepository.existeConquista(usuarioId, DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO))
                .thenReturn(true);

        useCase().executar(evento(usuarioId, StatusCofrinho.CONCLUIDO, CategoriaInvestimento.ECONOMIA));

        verify(conquistaRepository, never()).salvar(any());
    }

    @Test
    void deveDesbloquearStreakQuatroSemanasQuandoSequenciaAtingeQuatro() {
        UUID usuarioId = UUID.randomUUID();
        when(perfilXPRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Optional.of(perfilComStreak(usuarioId, 4)));

        useCase().executar(evento(usuarioId, StatusCofrinho.ATIVO, CategoriaInvestimento.ECONOMIA));

        ArgumentCaptor<ConquistaDesbloqueada> captor = ArgumentCaptor.forClass(ConquistaDesbloqueada.class);
        verify(conquistaRepository, times(1)).salvar(captor.capture());
        assertEquals(DefinicaoConquista.STREAK_QUATRO_SEMANAS, captor.getValue().getDefinicao());
    }

    @Test
    void naoDeveDesbloquearStreakQuandoSequenciaMenorQueQuatro() {
        UUID usuarioId = UUID.randomUUID();
        when(perfilXPRepository.buscarPorUsuarioId(usuarioId))
                .thenReturn(Optional.of(perfilComStreak(usuarioId, 3)));

        useCase().executar(evento(usuarioId, StatusCofrinho.ATIVO, CategoriaInvestimento.ECONOMIA));

        verify(conquistaRepository, never()).salvar(any());
    }
}
