package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarRankingXPUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.PosicaoRanking;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ranking")
public class RankingXPController {

    private final BuscarRankingXPUseCase buscarRankingXPUseCase;

    public RankingXPController(BuscarRankingXPUseCase buscarRankingXPUseCase) {
        this.buscarRankingXPUseCase = buscarRankingXPUseCase;
    }

    @GetMapping
    public ResponseEntity<List<PosicaoRanking>> buscarTop(@RequestParam(defaultValue = "10") int limite) {
        return ResponseEntity.ok(buscarRankingXPUseCase.executar(limite));
    }
}
