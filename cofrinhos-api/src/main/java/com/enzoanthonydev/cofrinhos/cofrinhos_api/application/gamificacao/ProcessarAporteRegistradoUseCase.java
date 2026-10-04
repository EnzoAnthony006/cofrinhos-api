package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.CalculadoraXPService;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProcessarAporteRegistradoUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessarAporteRegistradoUseCase.class);

    private final PerfilXPRepository perfilXPRepository;
    private final RankingXPRepository rankingXPRepository;
    private final CalculadoraXPService calculadoraXPService = new CalculadoraXPService();

    public ProcessarAporteRegistradoUseCase(PerfilXPRepository perfilXPRepository,
                                            RankingXPRepository rankingXPRepository) {
        this.perfilXPRepository = perfilXPRepository;
        this.rankingXPRepository = rankingXPRepository;
    }

    public void executar(AporteRegistradoEvent evento) {
        PerfilXP perfilXP = perfilXPRepository.buscarPorUsuarioId(evento.usuarioId())
                .orElseGet(() -> PerfilXP.iniciar(evento.usuarioId()));

        LocalDate dataAtividade = evento.dataRegistro().toLocalDate();
        perfilXP.registrarAtividadeStreak(dataAtividade);

        int semanaDeStreak = perfilXP.getStreakGlobal().getSequenciaAtual();
        int xpGanho = calculadoraXPService.calcular(Dinheiro.de(evento.valor()), semanaDeStreak);
        perfilXP.adicionarXP(xpGanho);

        perfilXPRepository.salvar(perfilXP);
        atualizarRanking(perfilXP);
    }

    private void atualizarRanking(PerfilXP perfilXP) {
        try {
            rankingXPRepository.atualizarXP(perfilXP.getUsuarioId(), perfilXP.getXpTotal());
        } catch (RuntimeException e) {
            log.warn("Não foi possível atualizar o ranking de XP do usuário {}", perfilXP.getUsuarioId(), e);
        }
    }
}
