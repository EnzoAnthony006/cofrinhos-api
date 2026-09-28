package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.CategoriaInvestimento;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.StatusCofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.ConquistaDesbloqueada;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.DefinicaoConquista;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VerificarConquistasUseCase {

    private static final int STREAK_MINIMA_PARA_CONQUISTA = 4;

    private final ConquistaRepository conquistaRepository;
    private final PerfilXPRepository perfilXPRepository;

    public VerificarConquistasUseCase(ConquistaRepository conquistaRepository,
                                      PerfilXPRepository perfilXPRepository) {
        this.conquistaRepository = conquistaRepository;
        this.perfilXPRepository = perfilXPRepository;
    }

    public void executar(AporteRegistradoEvent evento) {
        verificarPrimeiroCofrinhoConcluido(evento);
        verificarPrimeiraReservaEmergenciaCompleta(evento);
        verificarStreakQuatroSemanas(evento.usuarioId());
    }

    private void verificarPrimeiroCofrinhoConcluido(AporteRegistradoEvent evento) {
        if (evento.statusCofrinho() != StatusCofrinho.CONCLUIDO) {
            return;
        }
        desbloquearSeNecessario(evento.usuarioId(), DefinicaoConquista.PRIMEIRO_COFRINHO_CONCLUIDO);
    }

    private void verificarPrimeiraReservaEmergenciaCompleta(AporteRegistradoEvent evento) {
        if (evento.statusCofrinho() != StatusCofrinho.CONCLUIDO) {
            return;
        }
        if (evento.categoriaCofrinho() != CategoriaInvestimento.RESERVA_EMERGENCIA) {
            return;
        }
        desbloquearSeNecessario(evento.usuarioId(), DefinicaoConquista.PRIMEIRA_RESERVA_EMERGENCIA_COMPLETA);
    }

    private void verificarStreakQuatroSemanas(UUID usuarioId) {
        PerfilXP perfilXP = perfilXPRepository.buscarPorUsuarioId(usuarioId).orElse(null);
        if (perfilXP == null) {
            return;
        }
        if (perfilXP.getStreakGlobal().getSequenciaAtual() < STREAK_MINIMA_PARA_CONQUISTA) {
            return;
        }
        desbloquearSeNecessario(usuarioId, DefinicaoConquista.STREAK_QUATRO_SEMANAS);
    }

    private void desbloquearSeNecessario(UUID usuarioId, DefinicaoConquista definicao) {
        if (conquistaRepository.existeConquista(usuarioId, definicao)) {
            return;
        }
        ConquistaDesbloqueada conquista = ConquistaDesbloqueada.desbloquear(usuarioId, definicao);
        conquistaRepository.salvar(conquista);
    }
}
