package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.aporte.Aporte;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.aporte.RegistrarAporteUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cofrinhos/{cofrinhoId}/aportes")
public class AporteController {

    private final RegistrarAporteUseCase registrarAporteUseCase;

    public AporteController(RegistrarAporteUseCase registrarAporteUseCase) {
        this.registrarAporteUseCase = registrarAporteUseCase;
    }

    @PostMapping
    public ResponseEntity<AporteResponse> registrar(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID cofrinhoId,
            @Valid @RequestBody RegistrarAporteRequest request
    ) {
        Aporte aporte = registrarAporteUseCase.executar(
                cofrinhoId,
                UsuarioAutenticado.id(jwt),
                Dinheiro.de(request.valor())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(AporteResponse.de(aporte));
    }
}
