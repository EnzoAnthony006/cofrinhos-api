package com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.CalculadoraXPService;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.AporteRegistradoEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProcessarAporteRegistradoUseCase {

    private final PerfilXPRepository perfilXPRepository;
    private final CalculadoraXPService calculadoraXPService = new CalculadoraXPService();

    public ProcessarAporteRegistradoUseCase(PerfilXPRepository perfilXPRepository) {
        this.perfilXPRepository = perfilXPRepository;
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
    }
}
