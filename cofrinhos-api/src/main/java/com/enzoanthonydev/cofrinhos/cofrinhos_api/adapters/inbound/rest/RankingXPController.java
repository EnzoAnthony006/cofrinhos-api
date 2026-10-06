package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarRankingXPUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PosicaoRanking;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingXPController {

    private static final Duration INTERVALO_ATUALIZACAO = Duration.ofSeconds(2);

    private final BuscarRankingXPUseCase buscarRankingXPUseCase;

    public RankingXPController(BuscarRankingXPUseCase buscarRankingXPUseCase) {
        this.buscarRankingXPUseCase = buscarRankingXPUseCase;
    }

    @GetMapping
    public ResponseEntity<List<PosicaoRanking>> buscarTop(@RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(buscarRankingXPUseCase.executar(limite));
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<PosicaoRanking>> acompanharRanking(@RequestParam(defaultValue = "10") int limite) {
        return Flux.interval(Duration.ZERO, INTERVALO_ATUALIZACAO)
                .onBackpressureDrop()
                .concatMap(tick -> Mono
                        .fromCallable(() -> buscarRankingXPUseCase.executar(limite))
                        .subscribeOn(Schedulers.boundedElastic()))
                .distinctUntilChanged();
    }
}
