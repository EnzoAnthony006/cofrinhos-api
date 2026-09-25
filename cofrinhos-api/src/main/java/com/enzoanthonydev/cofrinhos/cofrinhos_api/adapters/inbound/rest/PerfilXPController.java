package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.gamificacao.PerfilXP;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.gamificacao.BuscarPerfilXPUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/perfis-xp")
public class PerfilXPController {

    private final BuscarPerfilXPUseCase buscarPerfilXPUseCase;

    public PerfilXPController(BuscarPerfilXPUseCase buscarPerfilXPUseCase) {
        this.buscarPerfilXPUseCase = buscarPerfilXPUseCase;
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<PerfilXPResponse> buscarPorUsuarioId(@PathVariable UUID usuarioId) {
        PerfilXP perfilXP = buscarPerfilXPUseCase.executar(usuarioId);
        return ResponseEntity.ok(PerfilXPResponse.de(perfilXP));
    }
}
