package com.enzoanthonydev.cofrinhos.cofrinhos_api.adapters.inbound.rest;

import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Cofrinho;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.Domain.Dinheiro;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.BuscarCofrinhoUseCase;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CofrinhoResumo;
import com.enzoanthonydev.cofrinhos.cofrinhos_api.application.CriarCofrinhoUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cofrinhos")
public class CofrinhoController {

    private final CriarCofrinhoUseCase criarCofrinhoUseCase;
    private final BuscarCofrinhoUseCase buscarCofrinhoUseCase;

    public CofrinhoController(CriarCofrinhoUseCase criarCofrinhoUseCase, BuscarCofrinhoUseCase buscarCofrinhoUseCase) {
        this.criarCofrinhoUseCase = criarCofrinhoUseCase;
        this.buscarCofrinhoUseCase = buscarCofrinhoUseCase;
    }

    @PostMapping
    public ResponseEntity<CofrinhoResponse> criar(@AuthenticationPrincipal Jwt jwt,
                                                  @Valid @RequestBody CriarCofrinhoRequest request) {
        Cofrinho cofrinho = criarCofrinhoUseCase.executar(
                UsuarioAutenticado.id(jwt),
                request.nome(),
                request.descricao(),
                request.categoria(),
                Dinheiro.de(request.valorMeta())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(CofrinhoResponse.de(cofrinho));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CofrinhoResponse> buscarPorId(@AuthenticationPrincipal Jwt jwt,
                                                        @PathVariable UUID id) {
        CofrinhoResumo resumo = buscarCofrinhoUseCase.executar(id, UsuarioAutenticado.id(jwt));
        return ResponseEntity.ok(CofrinhoResponse.de(resumo));
    }
}