package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarConquistasUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarPerfilXPUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarPosicaoNoRankingUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PosicaoRanking;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final BuscarPerfilXPUseCase buscarPerfilXPUseCase;
    private final BuscarConquistasUseCase buscarConquistasUseCase;
    private final BuscarPosicaoNoRankingUseCase buscarPosicaoNoRankingUseCase;

    public DashboardController(BuscarPerfilXPUseCase buscarPerfilXPUseCase,
                               BuscarConquistasUseCase buscarConquistasUseCase,
                               BuscarPosicaoNoRankingUseCase buscarPosicaoNoRankingUseCase) {
        this.buscarPerfilXPUseCase = buscarPerfilXPUseCase;
        this.buscarConquistasUseCase = buscarConquistasUseCase;
        this.buscarPosicaoNoRankingUseCase = buscarPosicaoNoRankingUseCase;
    }

    @GetMapping("/{usuarioId}")
    public Mono<DashboardResponse> buscar(@PathVariable UUID usuarioId) {
        Mono<PerfilXPResponse> perfil = Mono
                .fromCallable(() -> buscarPerfilXPUseCase.buscarSeExistir(usuarioId)
                        .orElseGet(() -> PerfilXP.iniciar(usuarioId)))
                .map(PerfilXPResponse::de)
                .subscribeOn(Schedulers.boundedElastic());

        Mono<List<ConquistaResponse>> conquistas = Mono
                .fromCallable(() -> buscarConquistasUseCase.executar(usuarioId).stream()
                        .map(ConquistaResponse::de)
                        .toList())
                .subscribeOn(Schedulers.boundedElastic());

        Mono<Optional<PosicaoRanking>> posicao = Mono
                .fromCallable(() -> buscarPosicaoNoRankingUseCase.executar(usuarioId))
                .subscribeOn(Schedulers.boundedElastic());

        return Mono.zip(perfil, conquistas, posicao)
                .map(tupla -> new DashboardResponse(
                        usuarioId,
                        tupla.getT1(),
                        tupla.getT2(),
                        tupla.getT3().orElse(null)
                ));
    }
}
